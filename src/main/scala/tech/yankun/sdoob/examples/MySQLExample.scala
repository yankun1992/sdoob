package tech.yankun.sdoob.examples

import tech.yankun.sdoob.driver.PoolOptions
import tech.yankun.sdoob.driver.command.{CloseConnectionCommand, SdoobSimpleQueryCommand, SimpleQueryCommand}
import tech.yankun.sdoob.driver.mysql.codec.SdoobSimpleQueryCommandCodec
import tech.yankun.sdoob.driver.mysql.{MySQLClient, MySQLConnectOptions, MySQLPool}

object MySQLExample {
  def main(args: Array[String]): Unit = {
    testClient()
  }

  def testClient(): Unit = {
    val connectOptions = MySQLConnectOptions.fromUri("jdbc:mysql://yankun:7890@localhost:3306/test")
    val client = new MySQLClient(connectOptions)
    client.connect()
    assert(client.isConnected)
    client.init()
    while (!client.isAuthenticated) {
      client.channelRead()
    }
    //    client.write(SimpleQueryCommand("select /*!40001 SQL_NO_CACHE */ * from test"))
    client.write(SdoobSimpleQueryCommand("select /*!40001 SQL_NO_CACHE */ * from test where 0=1"))
    val currentCodec = client.currentCodec.asInstanceOf[SdoobSimpleQueryCommandCodec]
    while (!client.codecCompleted) {
      client.channelRead()
      if (currentCodec.getTotalDecodeLen >= 64 * 1024 * 1024) {
        val length = currentCodec.getTotalDecodeLen
        val partition = currentCodec.moveRows
        println(s"partition rows ${partition.length} with decode payload length ${length}")
      }
    }
    client.write(CloseConnectionCommand)

    while (!client.codecCompleted) {
      client.channelRead()
    }
  }

  def testPool(): Unit = {
    val connectOptions = MySQLConnectOptions.fromUri("jdbc:mysql://yankun:7890@localhost:3306/test")
    val poolOptions = new PoolOptions()
    poolOptions.setSize(16)
    val pool = new MySQLPool(poolOptions, connectOptions)
    val start = System.currentTimeMillis()
    for (elem <- 0 to 200000) {
      val values = (elem * 500 until (elem + 1) * 500).map(i => s"(${i},${i * 2},'${i}')").mkString(",")
      pool.getReadyClient() match {
        case Some(client) =>
          client.write(SimpleQueryCommand(s"insert into test(id, double_id, str_id) values ${values}"))
        //          client.write(SimpleQueryCommand("SELECT /*!40001 SQL_NO_CACHE */ * FROM TEST"))
        case None =>
      }
    }
    val end = System.currentTimeMillis()
    println(s"time spend ${(end - start) / 1000}")
  }
}
