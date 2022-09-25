package tech.yankun.sdoob.driver.command

import tech.yankun.sdoob.driver.mysql.protocol.ColumnDefinition

case class SimpleQueryCommand(override val sql: String, singleton: Boolean = false,
                              override val autoCommit: Boolean = true)
  extends QueryCommandBase(autoCommit) {
  private val rows: collection.mutable.ArrayBuffer[Array[Any]] = collection.mutable.ArrayBuffer.empty
  private var definition: Array[ColumnDefinition] = _

  def addRow(row: Array[Any]): Unit = rows.append(row)

  def moveRows: Array[Array[Any]] = {
    val rs = rows.toArray
    rows.clear()
    rs
  }

  def setColumnDefinition(columnDefinitions: Array[ColumnDefinition]): Unit = definition = columnDefinition

  def columnDefinition: Array[ColumnDefinition] = definition
}
