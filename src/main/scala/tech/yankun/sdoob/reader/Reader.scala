package tech.yankun.sdoob.reader

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.log4s.getLogger
import tech.yankun.sdoob.args.AppArgs
import tech.yankun.sdoob.driver.SqlConnectOptions

abstract class Reader(val connectOptions: SqlConnectOptions, val appArgs: AppArgs) extends Serializable {
  protected val logger = getLogger

  def read(spark: SparkSession): DataFrame
}
