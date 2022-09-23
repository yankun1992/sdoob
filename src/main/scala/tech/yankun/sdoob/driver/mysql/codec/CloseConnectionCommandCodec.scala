package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.log4s.getLogger
import tech.yankun.sdoob.driver.Client
import tech.yankun.sdoob.driver.command.CloseConnectionCommand
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.protocol.CommandType

class CloseConnectionCommandCodec(cmd: CloseConnectionCommand.type)
  extends CommandCodec[CloseConnectionCommand.type, MySQLClient](cmd) {

  import CloseConnectionCommandCodec._

  private[this] val logger = getLogger

  override def encode(client: MySQLClient): Unit = {
    super.encode(client)
    sendQuitCommand()
    client.setStatus(Client.ST_CLIENT_CLOSED)
    logger.debug(s"client[${client.getClientId}] send COM_QUIT command")
  }


  override def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {
    logger.warn(s"client[${client.getClientId}] has closed")
  }

  private def sendQuitCommand(): Unit = {
    val packet = client.getByteBuf()
    // packet header
    packet.writeMediumLE(PAYLOAD_LENGTH)
    packet.writeByte(sequenceId)

    // payload
    packet.writeByte(CommandType.COM_QUIT)
    client.sendPacket(packet)
    client.release(packet)
  }
}

object CloseConnectionCommandCodec {
  protected val PAYLOAD_LENGTH: Int = 1
}
