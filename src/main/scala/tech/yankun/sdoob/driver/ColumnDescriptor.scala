package tech.yankun.sdoob.driver

import java.sql.JDBCType

trait ColumnDescriptor {
  /** the column name */
  def name: String

  /** whether the column is an array */
  def isArray: Boolean

  /** vendor-specific name of the column type, or [[Null]] if unknown */
  def typeName: String

  /** the most appropriate [[JDBCType]] */
  def jdbcType: JDBCType
}
