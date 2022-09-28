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

package tech.yankun.sdoob.driver.pg.protocol

import tech.yankun.sdoob.driver.pg.datatype.DataType
import tech.yankun.sdoob.driver.{ColumnDescriptor, DataFormat}

import java.sql.JDBCType

/**
 * Postgresql column descriptor
 */
case class ColumnDesc(fieldName: String, relationId: Int, dataType: DataType, dataFormat: DataFormat,
                      relationAttributeNo: Short, length: Short, typeModifier: Int)
  extends ColumnDescriptor {
  /** the column name */
  override def name: String = fieldName

  /** whether the column is an array */
  override def isArray: Boolean = dataType.array

  /** vendor-specific name of the column type, or [[Null]] if unknown */
  override def typeName: String = ???

  /** the most appropriate [[JDBCType]] */
  override def jdbcType: JDBCType = dataType.jdbcType
}

object ColumnDesc {
  val EMPTY_COLS: Array[ColumnDesc] = new Array[ColumnDesc](0)
}