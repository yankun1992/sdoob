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


package tech.yankun.sdoob.driver.pg.datatype

import java.sql.JDBCType

case class DataType(id: Int, encodingType: Class[_], jdbcType: JDBCType, supportsBinary: Boolean = true, array: Boolean = false)

object DataType {
  val BOOL: DataType = DataType(16, classOf[Boolean], JDBCType.BOOLEAN)
  val BOOL_ARRAY: DataType = DataType(1000, classOf[Array[Boolean]], JDBCType.BOOLEAN)
  val INT2: DataType = DataType(21, classOf[Short], JDBCType.SMALLINT, array = classOf[Number].isArray)
  val INT2_ARRAY: DataType = DataType(1005, classOf[Array[Short]], JDBCType.SMALLINT, array = classOf[Array[Number]].isArray)
  val INT4: DataType = DataType(23, classOf[Int], JDBCType.INTEGER)
  val INT4_ARRAY: DataType = DataType(1007, classOf[Array[Int]], JDBCType.INTEGER, array = true)
  val INT8: DataType = DataType(20, classOf[Long], JDBCType.BIGINT)
  val INT8_ARRAY: DataType = DataType(1016, classOf[Array[Long]], JDBCType.BIGINT, array = true)
  val FLOAT4: DataType = DataType(700, classOf[Float], JDBCType.REAL)
  val FLOAT4_ARRAY: DataType = DataType(1021, classOf[Array[Float]], JDBCType.REAL, array = true)
  val FLOAT8: DataType = DataType(701, classOf[Double], JDBCType.DOUBLE)
  val FLOAT8_ARRAY: DataType = DataType(1022, classOf[Array[Double]], JDBCType.DOUBLE, array = true)
  val NUMERIC: DataType = DataType(1700, classOf[Number], JDBCType.NUMERIC)
  val NUMERIC_ARRAY: DataType = DataType(1231, classOf[Array[Number]], JDBCType.NUMERIC, array = true)
  //  val MONEY: DataType = DataType(790, )


  def valueOf(typeOid: Int): DataType = {
    ???
  }
}