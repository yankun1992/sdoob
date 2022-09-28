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

package tech.yankun.sdoob.driver.pg.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.SimpleQueryCommand
import tech.yankun.sdoob.driver.pg.PGClient
import tech.yankun.sdoob.driver.pg.utils.BufferUtils

class SimpleQueryCodec(cmd: SimpleQueryCommand) extends QueryCommandBaseCodec[SimpleQueryCommand](cmd) {

  override def encode(client: PGClient): Unit = {
    logger.debug(s"client[${client.getClientId}] encode ${cmd}")
    super.encode(client)
    val packet = client.getByteBuf()
    packet.writeByte(PGCommandCodec.QUERY)
    val lenPos = packet.writerIndex()
    packet.writeInt(0)
    BufferUtils.writeCString(packet, cmd.sql)
    // reset len
    packet.setInt(lenPos, packet.writerIndex() - lenPos)

    // send to server
    client.sendPacket(packet)
    client.release(packet)
  }

  override protected def decodeRowDesc(payload: ByteBuf): Unit = {
    super.decodeRowDesc(payload)
    //
  }

  override def decodeRow(payload: ByteBuf): Unit = {
    super.decodeRow(payload)
  }
}
