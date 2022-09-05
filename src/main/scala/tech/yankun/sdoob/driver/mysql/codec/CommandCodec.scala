package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.Command
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.protocol.Packets.{EOF_PACKET_HEADER, ERROR_PACKET_HEADER, OK_PACKET_HEADER}
import tech.yankun.sdoob.driver.mysql.utils.BufferUtils

import java.nio.charset.Charset

abstract class CommandCodec[C <: Command, L <: MySQLClient](val cmd: C) {
  var client: L = _

  var sequenceId: Int = _

  def readRestOfPacketString(payload: ByteBuf, charset: Charset): String =
    BufferUtils.readFixedLengthString(payload, payload.readableBytes(), charset)

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

  def decodePayload(payload: ByteBuf, payloadLength: Int): Unit

  def encode(client: L): Unit = {
    this.client = client
    this.sequenceId = 0
  }


}
