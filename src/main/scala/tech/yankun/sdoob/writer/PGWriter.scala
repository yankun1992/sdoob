package tech.yankun.sdoob.writer

import org.apache.spark.sql.{DataFrame, Row}
import tech.yankun.sdoob.driver.{PoolOptions, SqlConnectOptions}

class PGWriter(connectOptions: SqlConnectOptions, poolOptions: PoolOptions) extends Writer(connectOptions, poolOptions) {
  override def write(dataset: DataFrame, mode: WriteMode): Unit = {
    val schema = dataset.schema

    dataset.foreachPartition { partition: Iterator[Row] =>

    }
  }
}

object PGWriter {

}
