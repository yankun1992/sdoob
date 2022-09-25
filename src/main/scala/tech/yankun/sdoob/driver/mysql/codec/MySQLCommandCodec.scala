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
import tech.yankun.sdoob.driver.codec.CommandCodec
import tech.yankun.sdoob.driver.command.Command
import tech.yankun.sdoob.driver.mysql.datatype.DataType
import tech.yankun.sdoob.driver.mysql.protocol.Packets._
import tech.yankun.sdoob.driver.mysql.protocol.{CapabilitiesFlag, ColumnDefinition}
import tech.yankun.sdoob.driver.mysql.utils.BufferUtils
import tech.yankun.sdoob.driver.mysql.{MySQLClient, MySQLException}

import java.nio.charset.{Charset, StandardCharsets}

abstract class MySQLCommandCodec[C <: Command](cmd: C)
  extends CommandCodec[C, MySQLClient](cmd) {

  var sequenceId: Int = _

  def readRestOfPacketString(payload: ByteBuf, charset: Charset): String =
    BufferUtils.readFixedLengthString(payload, payload.readableBytes(), charset)

  def decodeColumnDefinition(payload: ByteBuf): ColumnDefinition = {
    val catalog = BufferUtils.readLengthEncodedString(payload, StandardCharsets.UTF_8)
    val schema = BufferUtils.readLengthEncodedString(payload, StandardCharsets.UTF_8)
    val table = BufferUtils.readLengthEncodedString(payload, StandardCharsets.UTF_8)
    val orgTable = BufferUtils.readLengthEncodedString(payload, StandardCharsets.UTF_8)
    val name = BufferUtils.readLengthEncodedString(payload, StandardCharsets.UTF_8)
    val orgName = BufferUtils.readLengthEncodedString(payload, StandardCharsets.UTF_8)
    val lengthOfFixedLengthFields = BufferUtils.readLengthEncodedInteger(payload)
    val characterSet = payload.readUnsignedShortLE
    val columnLength = payload.readUnsignedIntLE
    val `type` = DataType.valueOf(payload.readUnsignedByte)
    val flags = payload.readUnsignedShortLE
    val decimals = payload.readByte
    ColumnDefinition(catalog, schema, table, orgTable, name, orgName, characterSet, columnLength, `type`, flags, decimals)
  }

  def sendBytesAsPacket(payload: Array[Byte]): Unit = {
    val length = payload.length
    val packet = client.getByteBuf()
    packet.writeMediumLE(length)
    packet.writeByte(sequenceId)
    packet.writeBytes(payload)
    client.sendPacket(packet)
    client.release(packet)
  }

  def handleOkPacketOrErrorPacketPayload(payload: ByteBuf): Unit = {
    val header = payload.getUnsignedByte(payload.readerIndex)
    header match {
      case EOF_PACKET_HEADER =>
      case OK_PACKET_HEADER =>
      case ERROR_PACKET_HEADER =>
    }
  }

  def handErrorPacket(payload: ByteBuf): Unit = {
    val exception = decodeErrorPacket(payload)
    client.release(payload)
    client.handleCommandResponse(exception)
  }

  def decodeErrorPacket(payload: ByteBuf): MySQLException = {
    payload.skipBytes(1) // skip ERR packet header
    val errorCode = payload.readUnsignedShortLE()
    // CLIENT_PROTOCOL_41 capability flag will always be set
    payload.skipBytes(1) // SQL state marker will always be #
    val sqlState = BufferUtils.readFixedLengthString(payload, 5, StandardCharsets.UTF_8)
    val errorMsg = readRestOfPacketString(payload, StandardCharsets.UTF_8)
    MySQLException(errorMsg, errorCode, sqlState)
  }

  /** simplify the ok packet as those properties are actually not used for now */
  def decodeOkPacket(payload: ByteBuf): OkPacket = {
    payload.skipBytes(1) // skip header
    val affected = BufferUtils.readLengthEncodedInteger(payload)
    val lastId = BufferUtils.readLengthEncodedInteger(payload)
    val serverFlags = payload.readUnsignedShortLE()

    OkPacket(affected, lastId, serverFlags, 0, "", "")
  }

  def decodeEofPacket(payload: ByteBuf): EofPacket = {
    payload.skipBytes(1) // skip header
    val warnings = payload.readUnsignedShortLE()
    val serverFlags = payload.readUnsignedShortLE()
    EofPacket(warnings, serverFlags)
  }

  def decodePayload(payload: ByteBuf, payloadLength: Int): Unit

  override def encode(client: MySQLClient): Unit = {
    super.encode(client)
    this.sequenceId = 0
  }

  def skipEofPacketIfNeeded(payload: ByteBuf): Unit = {
    if (!isDeprecatingEofFlagEnabled) {
      payload.skipBytes(5)
    }
  }

  def isDeprecatingEofFlagEnabled: Boolean = {
    (client.clientCapabilitiesFlag & CapabilitiesFlag.CLIENT_DEPRECATE_EOF) != 0
  }


}
