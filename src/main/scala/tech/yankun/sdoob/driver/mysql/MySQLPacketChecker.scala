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

package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.checker.PacketChecker
import tech.yankun.sdoob.driver.checker.PacketChecker.PacketState

class MySQLPacketChecker extends PacketChecker {
  override def check(buffer: ByteBuf): PacketState = if (buffer.readableBytes() > 4) {
    val start = buffer.readerIndex()
    val length = buffer.getUnsignedMediumLE(start)
    val packetLength = length + 4
    if (buffer.readableBytes() > packetLength) PacketChecker.MORE_THAN_ONE_PACKET
    else if (buffer.readableBytes() == packetLength) PacketChecker.ONLY_ONE_PACKET
    else PacketChecker.NO_FULL_PACKET // buffer.readableBytes() < packetLength
  } else if (buffer.readableBytes() == 0) PacketChecker.NO_PACKET else PacketChecker.NO_FULL_PACKET

}
