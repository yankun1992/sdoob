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

package tech.yankun.sdoob.driver.pg

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.checker.PacketChecker
import tech.yankun.sdoob.driver.command.{Command, CommandResponse, InitCommand, SimpleQueryCommand}
import tech.yankun.sdoob.driver.pg.codec.{InitCommandCodec, PGCommandCodec, SimpleQueryCodec}
import tech.yankun.sdoob.driver.{Client, SqlConnectOptions}

class PGClient(options: SqlConnectOptions, parent: Option[PGPool] = None)
  extends Client[PGPool, PGCommandCodec[_ <: Command]](options, parent) {


  override val packetChecker: PacketChecker = new PGPacketChecker

  override def initializeConfiguration(options: SqlConnectOptions): Unit = {}

  override def write(command: Command): Unit = {
    val codec: PGCommandCodec[_ <: Command] = wrap(command)
    if (inflight.isEmpty) flightCodec = Some(codec)
    inflight.addLast(codec)
    codec.encode(this)
  }

  override def read(): CommandResponse = ???

  override def close(): Unit = ???

  override def init(): Unit = {
    val initCommand = InitCommand(options.getUser, options.getPassword, options.getDatabase, options.getProperties)
    write(initCommand)
    logger.info(s"client[${clientId}] send init message")
    super.init()
  }

  override protected def decodePacket(packet: ByteBuf): Unit = {
    val start = packet.readerIndex()
    val msgType = packet.getByte(start)
    val payloadLength = packet.getInt(start + 1) + 1
    val codec = inflight.peek()
    flightCodec = Some(codec)
    codec.decodePayload(packet, payloadLength)
    if (bufferRemain && buffer.isReadable) buffer.readerIndex(start + payloadLength)
  }

  override def wrap(cmd: Command): PGCommandCodec[_ <: Command] = cmd match {
    case command: InitCommand => new InitCommandCodec(command)
    case simpleQueryCommand: SimpleQueryCommand => new SimpleQueryCodec(simpleQueryCommand)
    case _ =>
      throw new IllegalStateException(s"not supported database command: ${cmd}")
  }
}

object PGClient {

}