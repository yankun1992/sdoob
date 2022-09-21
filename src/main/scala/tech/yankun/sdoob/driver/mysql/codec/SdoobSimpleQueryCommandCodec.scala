package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructField, StructType}
import tech.yankun.sdoob.driver.Dialect
import tech.yankun.sdoob.driver.command.{SdoobSimpleQueryCommand, SimpleQueryCommand}
import tech.yankun.sdoob.driver.mysql.codec.QueryCommandBaseCodec.{HANDLING_COLUMN_DEFINITION, HANDLING_ROW_DATA_OR_END_PACKET}
import tech.yankun.sdoob.driver.mysql.datatype.{DataFormat, RowValueCodec}
import tech.yankun.sdoob.driver.mysql.protocol.ColumnDefinition

import scala.collection.mutable

class SdoobSimpleQueryCommandCodec(cmd: SdoobSimpleQueryCommand, format: DataFormat = DataFormat.TEXT)
  extends SimpleQueryCommandCodec(SimpleQueryCommand(cmd.sql, cmd.singleton, cmd.autoCommit), format) {

  private var schema: StructType = _

  def getSchema: StructType = schema

  private var collector = mutable.ArrayBuffer.empty[Row]

  private var totalDecodeLen: Int = 0

  private var rowBuffer: mutable.ArrayBuffer[Any] = _

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
          case Some(value) => value
          case None =>
            Dialect.getSparkType(column.jdbcType.getVendorTypeNumber, 0, 0, ColumnDefinition.isUnsignedNumeric(column.flags))
        }
        StructField(column.name, datatype)
      }
      schema = StructType(fields)
      rowBuffer = new mutable.ArrayBuffer(columnDefinitions.length)
    }
  }

  override protected def decodeRow(length: Int, payload: ByteBuf): Unit = {
    // const
    val NULL: Short = 0xFB
    // decode result set row, TEXT format
    var index = 0
    while (index < length) {
      var value: Any = null
      if (payload.getUnsignedByte(payload.readerIndex()) == NULL) {
        payload.skipBytes(1)
      } else {
        val definition = columnDefinitions(index)
        // decode
        value = RowValueCodec.decodeText(definition.`type`, definition.characterSet, definition.flags, payload)
      }
      rowBuffer.append(value)
      index += 1
    }
    collector.append(Row.fromSeq(rowBuffer))
    rowBuffer.clear()
  }

  def getTotalDecodeLen: Int = totalDecodeLen

  def moveRows: Iterator[Row] = {
    totalDecodeLen = 0
    val iter = collector
    collector = mutable.ArrayBuffer.empty[Row]
    iter.iterator
  }

}

