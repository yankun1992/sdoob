package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{Row, RowGenerator}
import tech.yankun.sdoob.driver.Dialect
import tech.yankun.sdoob.driver.command.{SdoobSimpleQueryCommand, SimpleQueryCommand}
import tech.yankun.sdoob.driver.mysql.codec.QueryMySQLCommandBaseCodec.{HANDLING_COLUMN_DEFINITION, HANDLING_ROW_DATA_OR_END_PACKET}
import tech.yankun.sdoob.driver.mysql.datatype.DataFormat
import tech.yankun.sdoob.driver.mysql.protocol.ColumnDefinition

import scala.collection.mutable

class SdoobSimpleQueryMySQLCommandCodec(cmd: SdoobSimpleQueryCommand, format: DataFormat = DataFormat.TEXT)
  extends SimpleQueryMySQLCommandCodec(SimpleQueryCommand(cmd.sql, cmd.singleton, cmd.autoCommit), format) {

  private var schema: StructType = _

  def getSchema: StructType = schema

  private var collector = mutable.ArrayBuffer.empty[Row]

  private var totalDecodeLen: Int = 0

  override def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {
    super.decodePayload(payload, payloadLength)
    if (commandHandlerState == HANDLING_ROW_DATA_OR_END_PACKET) totalDecodeLen += payloadLength
  }

  override protected def handleColumnDefinitions(payload: ByteBuf): Unit = {
    super.handleColumnDefinitions(payload)
    if (commandHandlerState != HANDLING_COLUMN_DEFINITION) {
      val dialect = Dialect.get("mysql")
      val fields = for (column <- columnDefinitions) yield {
        val datatype = dialect.getSparkType(column.jdbcType.getVendorTypeNumber, column.jdbcType.getName, column.columnLength.toInt) match {
          case None =>
            val signed = ColumnDefinition.isUnsignedNumeric(column.flags)
            Dialect.getSparkType(column.jdbcType.getVendorTypeNumber, 0, 0, signed)
          case Some(value) => value
        }
        StructField(column.name, datatype)
      }
      schema = StructType(fields)
    }
  }

  override protected def decodeRow(length: Int, payload: ByteBuf): Unit = {
    val row = doDecodeRow(length, payload)
    collector.append(RowGenerator.generate(row))
  }

  def getTotalDecodeLen: Int = totalDecodeLen

  def moveRows: Iterator[Row] = {
    totalDecodeLen = 0
    val iter = collector
    collector = mutable.ArrayBuffer.empty[Row]
    iter.iterator
  }

}

