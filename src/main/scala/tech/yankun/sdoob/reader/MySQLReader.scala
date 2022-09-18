package tech.yankun.sdoob.reader

import org.apache.spark.sql.{DataFrame, SparkSession}

class MySQLReader(spark: SparkSession) extends Reader(spark) {


  override def read(): DataFrame = {
    val partitioner = ReaderPartitioner(2048)

    //    spark.createDataset(1 to 2048)

    ???
  }


}
