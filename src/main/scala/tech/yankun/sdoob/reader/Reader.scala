package tech.yankun.sdoob.reader

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class Reader(val spark: SparkSession) {
  def read(): DataFrame
}
