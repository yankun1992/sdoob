/*
 * Copyright (C) 2022  Yan Kun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.DataFormat
import tech.yankun.sdoob.driver.command.SimpleQueryCommand
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.codec.QueryCommandBaseCodec.HANDLING_COLUMN_DEFINITION
import tech.yankun.sdoob.driver.mysql.datatype.RowValueCodec
import tech.yankun.sdoob.driver.mysql.protocol.CommandType
import tech.yankun.sdoob.driver.mysql.protocol.Packets.{ERROR_PACKET_HEADER, OK_PACKET_HEADER}

class SimpleQueryCommandCodec(cmd: SimpleQueryCommand, format: DataFormat = DataFormat.TEXT)
  extends QueryCommandBaseCodec[SimpleQueryCommand](cmd, format) {

  override def encode(client: MySQLClient): Unit = {
    super.encode(client)
    val packet = client.getByteBuf()
    val startIdx = packet.writerIndex()
    packet.writeMediumLE(0)
    packet.writeByte(sequenceId)
    packet.writeByte(CommandType.COM_QUERY)
    packet.writeCharSequence(cmd.sql, client.encodingCharset)

    // set payload length
    val payloadLength = packet.writerIndex - startIdx - 4
    packet.setMediumLE(startIdx, payloadLength)

    client.sendPacket(packet)
    client.release(packet)
  }

  override protected def handleInitPacket(payload: ByteBuf): Unit = {
    val firstByte = payload.getUnsignedByte(payload.readerIndex())
    firstByte match {
      case OK_PACKET_HEADER =>
        val ok = decodeOkPacket(payload)
        client.release(payload)
        client.handleCommandResponse(ok)
      case ERROR_PACKET_HEADER =>
        handErrorPacket(payload)
      case 0xFB => ???
      case _ => handleResultsetColumnCountPacketBody(payload)
    }
  }

  override protected def handleColumnDefinitions(payload: ByteBuf): Unit = {
    super.handleColumnDefinitions(payload)
    if (commandHandlerState != HANDLING_COLUMN_DEFINITION) cmd.setColumnDefinition(columnDefinitions)
  }

  override protected def decodeRow(length: Int, payload: ByteBuf): Unit = {
    val row = doDecodeRow(length, payload)
    cmd.addRow(row)
  }

  protected def doDecodeRow(length: Int, payload: ByteBuf): Array[Any] = {
    // const
    val NULL: Short = 0xFB
    // decode result set row, TEXT format
    var index = 0
    val row = new Array[Any](length)
    while (index < length) {
      var value: Any = null
      if (payload.getUnsignedByte(payload.readerIndex()) == NULL) {
        payload.skipBytes(1)
      } else {
        val definition = columnDefinitions(index)
        // decode
        value = RowValueCodec.decodeText(definition.`type`, definition.characterSet, definition.flags, payload)
      }
      row(index) = value
      index += 1
    }
    row
  }
}
