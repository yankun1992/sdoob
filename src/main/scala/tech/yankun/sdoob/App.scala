package tech.yankun.sdoob

import org.apache.spark.sql.SparkSession
import tech.yankun.sdoob.driver.{ClientPool, PoolOptions}

object App {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("sdoob").enableHiveSupport().getOrCreate()

    val table = spark.table("").rdd

    table.foreachPartition { partition =>
      val poolOptions = new PoolOptions()
      poolOptions.setSize(16)

      val pool = ClientPool.createPoolByUri("", poolOptions)


    }


  }
}
