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

import java.nio.charset.StandardCharsets
import java.security.{MessageDigest, NoSuchAlgorithmException}

object MD5Authentication {
  private val HEX_ALPHABET = "0123456789abcdef".toCharArray

  private def toHex(bytes: Array[Byte]): String = {
    val hexChars = new Array[Char](bytes.length * 2)
    for (j <- bytes.indices) {
      val v = bytes(j) & 0xFF
      hexChars(j * 2) = HEX_ALPHABET(v >>> 4)
      hexChars(j * 2 + 1) = HEX_ALPHABET(v & 0x0F)
    }
    new String(hexChars)
  }

  def encode(username: String, password: String, salt: Array[Byte]): String = {
    val msgDigest: MessageDigest = try {
      MessageDigest.getInstance("MD5")
    } catch {
      case e: NoSuchAlgorithmException =>
        throw e
    }
    msgDigest.update(password.getBytes(StandardCharsets.UTF_8))
    msgDigest.update(username.getBytes(StandardCharsets.UTF_8))
    val digest = msgDigest.digest()

    val hexDigest: Array[Byte] = toHex(digest).getBytes(StandardCharsets.US_ASCII)
    msgDigest.update(hexDigest)
    msgDigest.update(salt)
    val passDigest = msgDigest.digest()

    "md5" + toHex(passDigest)
  }
}
