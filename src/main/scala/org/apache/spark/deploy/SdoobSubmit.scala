package org.apache.spark.deploy

object SdoobSubmit {
  def submit(args: Array[String]): Unit = {
    SparkSubmit.main(args)
  }
}
