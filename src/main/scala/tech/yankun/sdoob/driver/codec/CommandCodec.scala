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

package tech.yankun.sdoob.driver.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.Client
import tech.yankun.sdoob.driver.command.Command

/**
 * An command codec for encode and decode database message
 *
 * @param cmd database command
 * @tparam C type of command
 * @tparam L type of client
 */
abstract class CommandCodec[C <: Command, L <: Client[_, _]](val cmd: C) {
  var client: L = _

  /**
   * encode command to database message packet, and send to server by client
   *
   * @param client database client
   */
  def encode(client: L): Unit = {
    this.client = client
  }

  /**
   * decode message packet received from database
   *
   * @param payload       message packet payload
   * @param payloadLength payload length
   */
  def decodePayload(payload: ByteBuf, payloadLength: Int): Unit
}
