package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.Command
import tech.yankun.sdoob.driver.mysql.datatype.DataType
import tech.yankun.sdoob.driver.mysql.protocol.Packets._
import tech.yankun.sdoob.driver.mysql.protocol.{CapabilitiesFlag, ColumnDefinition}
import tech.yankun.sdoob.driver.mysql.utils.BufferUtils
import tech.yankun.sdoob.driver.mysql.{MySQLClient, MySQLException}

import java.nio.charset.{Charset, StandardCharsets}

abstract class CommandCodec[C <: Command, L <: MySQLClient](val cmd: C) {
  var client: L = _

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

  def encode(client: L): Unit = {
    this.client = client
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
