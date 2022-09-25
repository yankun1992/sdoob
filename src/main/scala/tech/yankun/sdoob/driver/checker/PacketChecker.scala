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

package tech.yankun.sdoob.driver.checker

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.checker.PacketChecker.PacketState

/**
 * A abstract to check database message whether complete
 */
trait PacketChecker {
  def check(buffer: ByteBuf): PacketState
}

object PacketChecker {
  type PacketState = Int
  /** message packet is not complete */
  val NO_FULL_PACKET: PacketState = 0
  /** only one complete message packet */
  val ONLY_ONE_PACKET: PacketState = 1
  /** more than one message packet */
  val MORE_THAN_ONE_PACKET: PacketState = 2

  val NO_PACKET: PacketState = 3
}
