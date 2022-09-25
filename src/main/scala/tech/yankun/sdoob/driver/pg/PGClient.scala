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

import tech.yankun.sdoob.driver.checker.PacketChecker
import tech.yankun.sdoob.driver.command.{Command, CommandResponse}
import tech.yankun.sdoob.driver.pg.codec.PGCommandCodec
import tech.yankun.sdoob.driver.{Client, SqlConnectOptions}

class PGClient(options: SqlConnectOptions, parent: Option[PGPool] = None)
  extends Client[PGPool, PGCommandCodec[_]](options, parent) {


  override val packetChecker: PacketChecker = new PGPacketChecker

  override def initializeConfiguration(options: SqlConnectOptions): Unit = {}

  override def write(command: Command): Unit = ???

  override def read(): CommandResponse = ???

  override def close(): Unit = ???

  override def currentCodec: Nothing = ???

  override def init(): Unit = ???

  override def channelRead(): Unit = ???

  override def wrap(cmd: Command): PGCommandCodec[_] = ???
}

object PGClient {

}