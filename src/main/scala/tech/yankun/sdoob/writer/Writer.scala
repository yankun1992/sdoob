package tech.yankun.sdoob.writer

import org.apache.spark.sql.DataFrame
import tech.yankun.sdoob.driver.{PoolOptions, SqlConnectOptions}


abstract class Writer(val connectOptions: SqlConnectOptions, val poolOptions: PoolOptions) {
  def write(dataset: DataFrame, mode: WriteMode): Unit
}
