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

package tech.yankun.sdoob

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.log4s.getLogger
import tech.yankun.sdoob.args.{AppArgs, ArgsConfig, SparkArgs}
import tech.yankun.sdoob.driver.PoolOptions
import tech.yankun.sdoob.driver.mysql.MySQLConnectOptions
import tech.yankun.sdoob.reader.{MySQLReader, Reader}
import tech.yankun.sdoob.writer.{MySQLWriter, WriteMode}

object CommandHandler {
  private val logger = getLogger

  def handleImport(argsConfig: ArgsConfig): Unit = {
    val appArgs = argsConfig.appArgs
    val dbType = getDatabaseType(appArgs)
    val reader: Reader = dbType match {
      case "mysql" =>
        val connectOptions = MySQLConnectOptions.fromUri("jdbc:mysql://yankun:7890@localhost:3306/test")
        MySQLReader(connectOptions, argsConfig.appArgs)
      case _ =>
        ???
    }

    val spark = createSparkSession(argsConfig.sparkArgs)

    val dataset = reader.read(spark)
    dataset.write.mode(SaveMode.Overwrite).parquet("D:\\trans_result\\test")
    dataset.show(20, truncate = false)

    spark.stop()

  }

  def handleExport(argsConfig: ArgsConfig): Unit = {
    val dbType = getDatabaseType(argsConfig.appArgs)

    val spark = createSparkSession(argsConfig.sparkArgs)


    val dataset = spark.read.format("").load("")


    dbType match {
      case "mysql" =>
        val connectOptions = new MySQLConnectOptions()
        val poolOptions = new PoolOptions()
        val writer = new MySQLWriter(connectOptions, poolOptions, argsConfig.appArgs)
        writer.write(dataset, WriteMode.INSERT)
      case "postgres" =>
        ???
    }

  }

  private def createSparkSession(sparkArgs: SparkArgs): SparkSession = {
    sparkArgs.name match {
      case Some(x) => SparkSession.builder().appName(x).getOrCreate()
      case None => SparkSession.builder().getOrCreate()
    }
  }

  private def getDatabaseType(appArgs: AppArgs): String = {
    val uri = appArgs.connect.get
    val dbType = if (uri.startsWith("jdbc:mysql") || uri.startsWith("jdbc:mariadb")) {
      "mysql"
    } else {
      throw new IllegalArgumentException(s"Unknown jdbc url [${uri}]")
    }
    dbType
  }
}
