package org.apache.spark.sql

import org.apache.spark.sql.catalyst.expressions.GenericRow

object RowGenerator {
  def generate(array: Array[Any]): Row = {
    new GenericRow(array)
  }
}
