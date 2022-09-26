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

package tech.yankun.sdoob.driver.pg.utils

import io.netty.buffer.ByteBuf

import java.nio.charset.{Charset, StandardCharsets}

object BufferUtils {
  private val ZERO: Byte = 0
  private val FIRST_HALF_BYTE_MASK: Int = 0x0F

  def readCString(buffer: ByteBuf, charset: Charset = StandardCharsets.UTF_8): String = {
    val len = buffer.bytesBefore(ZERO)
    val s = buffer.readCharSequence(len, charset).toString
    buffer.skipBytes(1)
    s
  }

  def writeCString(buffer: ByteBuf, s: String, charset: Charset = StandardCharsets.UTF_8): Unit = {
    buffer.writeCharSequence(s, charset)
    buffer.writeByte(ZERO)
  }

  def writeCString(buffer: ByteBuf, bytes: Array[Byte]): Unit = {
    buffer.writeBytes(bytes, 0, bytes.length)
    buffer.writeByte(ZERO)
  }

  def writeHexString(buffer: ByteBuf, to: ByteBuf): Int = {
    ???
  }
}
