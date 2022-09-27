/*
 * Copyright (C) 2022  Yan Kun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tech.yankun.sdoob.reader

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.log4s.getLogger
import tech.yankun.sdoob.args.AppArgs
import tech.yankun.sdoob.driver.command.{CloseConnectionCommand, Command, SdoobSimpleQueryCommand}
import tech.yankun.sdoob.driver.mysql.codec.{MySQLCommandCodec, SdoobSimpleQueryCommandCodec}
import tech.yankun.sdoob.driver.mysql.{MySQLClient, MySQLConnectOptions}

class MySQLReader(connectOptions: MySQLConnectOptions, appArgs: AppArgs)
  extends Reader(connectOptions, appArgs) {
  private val sql: String = appArgs.table match {
    case Some(table) =>
      val columns = if (appArgs.columns.isEmpty) "* " else appArgs.columns.mkString(",") + " "
      "SELECT /*!40001 SQL_NO_CACHE */ " + columns + s"FROM $table " + appArgs.where.map(w => s"WHERE ${w}").getOrElse("")
    case None =>
      appArgs.query match {
        case Some(query) =>
          // check query
          if (appArgs.where.nonEmpty) {
            if (!query.contains("$CONDITIONS")) {
              throw new IllegalArgumentException("Your --where args is not empty, so a $CONDITIONS must include in --query")
            } else {
              query.replace("$CONDITIONS", appArgs.where.get)
            }
          } else {
            query
          }
        case None =>
          throw new IllegalArgumentException("import args error, you should use --table or --query to specify dataset to import")
      }
  }

  private val schemaDetectionSql: String = appArgs.table match {
    case Some(table) =>
      val columns = if (appArgs.columns.isEmpty) "* " else appArgs.columns.mkString(",") + " "
      "SELECT /*!40001 SQL_NO_CACHE */ " + columns + s"FROM $table where 0=1"
    case None =>
      ???
  }


  override def read(spark: SparkSession): DataFrame = {
    import spark.implicits._
    // detect dataset schema
    val driverHolder = MySQLReader.DriverHolder
    driverHolder.initAndAuth(connectOptions)
    driverHolder.write(SdoobSimpleQueryCommand(schemaDetectionSql))
    val currentCodec = driverHolder.currentCodec.asInstanceOf[SdoobSimpleQueryCommandCodec]
    driverHolder.completeCodec()
    val schema = currentCodec.getSchema
    driverHolder.close()

    val partitioner = ReaderPartitioner(128)
    val chunkIndex = spark.createDataset(0 until 128).rdd.map(k => k -> k).partitionBy(partitioner)

    val acc = spark.sparkContext.longAccumulator("empty partition")
    val parts = collection.mutable.ArrayBuffer.empty[DataFrame]

    val sqlBc = spark.sparkContext.broadcast(sql)

    while (acc.value == 0) {
      logger.warn("read round 256 partition")
      val rows = chunkIndex.mapPartitions { _: Iterator[(Int, Int)] =>
        val clientHolder = MySQLReader.ExecutorHolder
        if (clientHolder.readCompleted) {
          acc.add(1) // record empty partition size
          Iterator.empty
        } else {
          if (!clientHolder.isAuth) { // initial client if it is not initial
            clientHolder.initAndAuth(connectOptions)
            clientHolder.execute(sqlBc.value)
          }
          clientHolder.readRows
        }
      }
      val part = spark.createDataFrame(rows, schema = schema)
      part.persist(StorageLevel.DISK_ONLY_2)
      part.foreachPartition { _: Iterator[_] => {} }
      parts.append(part)
    }

    parts.reduce((a, b) => a union b)
  }

}

object MySQLReader extends Serializable {

  /**
   * mysql client holder
   */
  abstract class ClientHolder extends Serializable {
    protected val logger = getLogger

    protected var client: MySQLClient = _
    private var isClosed: Boolean = false
    private var options: MySQLConnectOptions = _

    private def init(connectOptions: MySQLConnectOptions): Unit = if (client == null)
      options = connectOptions
    else if (client != null && client.isClosed)
      options = connectOptions
    else
      throw new IllegalStateException("Can not reset connect options to active mysql client ")

    def initAndAuth(connectOptions: MySQLConnectOptions): Unit = this.synchronized {
      init(connectOptions)
      this.client = new MySQLClient(options)
      this.client.connect()
      assert(this.client.isConnected)
      this.client.init()
      while (!this.client.isAuthenticated) {
        this.client.channelRead()
      }
      logger.warn("create and auth mysql client")
    }

    def close(): Unit = this.synchronized {
      if (client != null && !client.isClosed) {
        client.write(CloseConnectionCommand)
        while (!client.codecCompleted) client.channelRead()
        isClosed = true
      }
    }

    def isInit = options != null

    def isAuth = client != null && client.isAuthenticated

    def isClose: Boolean = isClosed

    def completeCodec(): Unit = this.synchronized {
      while (!client.codecCompleted) client.channelRead()
    }

    def write(command: Command): Unit = this.synchronized {
      client.write(command)
    }

    def currentCodec: MySQLCommandCodec[_] = client.currentCodec

    override def finalize(): Unit = {
      if (client != null && !client.isClosed) close()
      super.finalize()
    }

  }

  protected trait Executor extends Serializable {
    def execute(sql: String): Unit

    def readRows: Iterator[Row]

    def readCompleted: Boolean
  }

  /**
   * mysql client used by spark driver code
   */
  object DriverHolder extends ClientHolder

  /** a spark executor(jvm instance) have only one client instance, spark tasks share this client. */
  object ExecutorHolder extends ClientHolder with Executor {
    private var executorQuerySend: Boolean = false
    private var readRowsEnd: Boolean = false
    private var sdoobSimpleQueryCommandCodec: SdoobSimpleQueryCommandCodec = _

    def readCompleted: Boolean = readRowsEnd

    def execute(sql: String): Unit = this.synchronized {
      if (!executorQuerySend) {
        executorQuerySend = true
        client.write(SdoobSimpleQueryCommand(sql))
        sdoobSimpleQueryCommandCodec = currentCodec.asInstanceOf[SdoobSimpleQueryCommandCodec]
        logger.warn("execute fetch sql")
      }
    }

    def readRows: Iterator[Row] = this.synchronized {
      while (!client.codecCompleted && sdoobSimpleQueryCommandCodec.getTotalDecodeLen < 64 * 1024 * 1024) client.channelRead()
      val rows = sdoobSimpleQueryCommandCodec.moveRows
      if (client.codecCompleted) {
        readRowsEnd = true
        if (!client.isClosed) {
          close()
          logger.warn("executor mysql client closed")
        }
      }
      logger.warn("partition parser completed")
      rows
    }
  }

  def apply(connectOptions: MySQLConnectOptions, appArgs: AppArgs): MySQLReader = new MySQLReader(connectOptions, appArgs)
}
