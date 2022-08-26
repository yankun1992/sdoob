package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.{Client, Command}

abstract class CommandCodec[C <: Command](val cmd: C) {
  var client: Client = _

  var sequenceId: Int = _

  def decodePayload(payload: ByteBuf, payloadLength: Int): Unit

  def encode(client: Client): Unit = {
    this.client = client
    this.sequenceId = 0
  }


}
