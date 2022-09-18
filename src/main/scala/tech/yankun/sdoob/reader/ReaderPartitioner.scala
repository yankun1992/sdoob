package tech.yankun.sdoob.reader

import org.apache.spark.Partitioner

class ReaderPartitioner(val num: Int) extends Partitioner {
  override def numPartitions: Int = num

  override def getPartition(key: Any): Int = key.asInstanceOf[Int]
}

object ReaderPartitioner {
  def apply(num: Int): ReaderPartitioner = new ReaderPartitioner(num)
}