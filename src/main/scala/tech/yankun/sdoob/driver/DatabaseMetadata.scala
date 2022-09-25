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

/**
 * Contains static metadata about the backend database server
 */
trait DatabaseMetadata {
  /** @return The product name of the backend database server */
  def productName: String

  /**
   * @return The full version string for the backend database server.
   *         This may be useful for for parsing more subtle aspects of the version string.
   *         For simple information like database major and minor version, use [[majorVersion]]
   *         and [[minorVersion]] instead.
   */
  def fullVersion: String

  /** @return The major version of the backend database server */
  def majorVersion: Int

  /** @return The minor version of the backend database server */
  def minorVersion: Int
}
