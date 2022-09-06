package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.SimpleQueryCommand
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.datatype.DataFormat
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
}
