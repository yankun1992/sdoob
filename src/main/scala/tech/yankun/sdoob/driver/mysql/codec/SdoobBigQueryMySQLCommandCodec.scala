package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.SdoobBigQueryCommand
import tech.yankun.sdoob.driver.mysql.MySQLClient

class SdoobBigQueryMySQLCommandCodec(cmd: SdoobBigQueryCommand)
  extends MySQLCommandCodec[SdoobBigQueryCommand](cmd) {

  override def encode(client: MySQLClient): Unit = {
    super.encode(client)
    client.sendPacket(cmd.packet)
    cmd.packet.release()
  }

  override def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {

  }
}
