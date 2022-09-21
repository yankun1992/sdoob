package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructField, StructType}
import tech.yankun.sdoob.driver.command.{SdoobSimpleQueryCommand, SimpleQueryCommand}
import tech.yankun.sdoob.driver.mysql.codec.QueryCommandBaseCodec.{HANDLING_COLUMN_DEFINITION, HANDLING_ROW_DATA_OR_END_PACKET}
import tech.yankun.sdoob.driver.mysql.datatype.{DataFormat, RowValueCodec}
import tech.yankun.sdoob.utils.Utils

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
      schema = StructType(columnDefinitions.map(column => StructField(name = column.name, dataType = Utils.jdbcType2sparkType(column.jdbcType))))
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

