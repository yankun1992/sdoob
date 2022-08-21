package tech.yankun.sdoob

import org.apache.spark.sql.SparkSession

object App {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("sdoob").enableHiveSupport().getOrCreate()

    val table = spark.table("")

    //    table.foreachPartition { partition =>
    //      //      val pool = ClientPool.createPoolByUri("")
    //      //      while (partition.hasNext) {
    //      //        val client = pool.waitReadyClient()
    //      //        // generate command
    //      //        // write command
    //      //        //        client.write()
    //      //      }
    //      partition.foreach(println)
    //
    //      //      val address = InetSocketAddress.createUnresolved("", 8086)
    //      //      val socket = SocketChannel.open()
    //      //      socket.connect(address)
    //      //      socket.configureBlocking(false)
    //
    //    }


  }
}
