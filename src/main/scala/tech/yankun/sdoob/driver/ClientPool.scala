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

package tech.yankun.sdoob.driver

import io.netty.buffer.PooledByteBufAllocator

trait ClientPool {
  def alloc: PooledByteBufAllocator
}

object ClientPool {
  def createPoolByUri(uri: String, poolOptions: PoolOptions): ClientPool = {
    ???
  }

  def apply(connectOptions: SqlConnectOptions, poolOptions: PoolOptions): ClientPool = ???
}