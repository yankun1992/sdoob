package tech.yankun.sdoob.driver.mysql.protocol

import tech.yankun.sdoob.driver.ColumnDescriptor
import tech.yankun.sdoob.driver.mysql.datatype.DataType

import java.sql.JDBCType

/** https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_query_response_text_resultset_column_definition.html */
case class ColumnDefinition(
                             catalog: String,
                             schema: String,
                             table: String,
                             orgTable: String,
                             override val name: String,
                             orgName: String,
                             characterSet: Int,
                             columnLength: Long,
                             `type`: DataType,
                             flags: Int,
                             decimals: Byte
                           ) extends ColumnDescriptor {
  override def isArray: Boolean = false

  override def typeName: String = `type`.toString

  override def jdbcType: JDBCType = `type`.jdbcType

}


object ColumnDefinition {
  /*
      Type of column definition
      https://dev.mysql.com/doc/dev/mysql-server/latest/binary__log__types_8h.html#aab0df4798e24c673e7686afce436aa85
     */
  object ColumnType {
    val MYSQL_TYPE_DECIMAL = 0x00
    val MYSQL_TYPE_TINY = 0x01
    val MYSQL_TYPE_SHORT = 0x02
    val MYSQL_TYPE_LONG = 0x03
    val MYSQL_TYPE_FLOAT = 0x04
    val MYSQL_TYPE_DOUBLE = 0x05
    val MYSQL_TYPE_NULL = 0x06
    val MYSQL_TYPE_TIMESTAMP = 0x07
    val MYSQL_TYPE_LONGLONG = 0x08
    val MYSQL_TYPE_INT24 = 0x09
    val MYSQL_TYPE_DATE = 0x0A
    val MYSQL_TYPE_TIME = 0x0B
    val MYSQL_TYPE_DATETIME = 0x0C
    val MYSQL_TYPE_YEAR = 0x0D
    val MYSQL_TYPE_VARCHAR = 0x0F
    val MYSQL_TYPE_BIT = 0x10
    val MYSQL_TYPE_JSON = 0xF5
    val MYSQL_TYPE_NEWDECIMAL = 0xF6
    val MYSQL_TYPE_ENUM = 0xF7
    val MYSQL_TYPE_SET = 0xF8
    val MYSQL_TYPE_TINY_BLOB = 0xF9
    val MYSQL_TYPE_MEDIUM_BLOB = 0xFA
    val MYSQL_TYPE_LONG_BLOB = 0xFB
    val MYSQL_TYPE_BLOB = 0xFC
    val MYSQL_TYPE_VAR_STRING = 0xFD
    val MYSQL_TYPE_STRING = 0xFE
    val MYSQL_TYPE_GEOMETRY = 0xFF

    /*
      Internal to MySQL Server
     */
    private val MYSQL_TYPE_NEWDATE = 0x0E
    private val MYSQL_TYPE_TIMESTAMP2 = 0x11
    private val MYSQL_TYPE_DATETIME2 = 0x12
    private val MYSQL_TYPE_TIME2 = 0x13
  }

  /*
      https://dev.mysql.com/doc/dev/mysql-server/latest/group__group__cs__column__definition__flags.html
     */
  object ColumnDefinitionFlags {
    val NOT_NULL_FLAG = 0x00000001
    val PRI_KEY_FLAG = 0x00000002
    val UNIQUE_KEY_FLAG = 0x00000004
    val MULTIPLE_KEY_FLAG = 0x00000008
    val BLOB_FLAG = 0x00000010
    val UNSIGNED_FLAG = 0x00000020
    val ZEROFILL_FLAG = 0x00000040
    val BINARY_FLAG = 0x00000080
    val ENUM_FLAG = 0x00000100
    val AUTO_INCREMENT_FLAG = 0x00000200
    val TIMESTAMP_FLAG = 0x00000400
    val SET_FLAG = 0x00000800
    val NO_DEFAULT_VALUE_FLAG = 0x00001000
    val ON_UPDATE_NOW_FLAG = 0x00002000
    val NUM_FLAG = 0x00008000
    val PART_KEY_FLAG = 0x00004000
    val GROUP_FLAG = 0x00008000
    val UNIQUE_FLAG = 0x00010000
    val BINCMP_FLAG = 0x00020000
    val GET_FIXED_FIELDS_FLAG = 0x00040000
    val FIELD_IN_PART_FUNC_FLAG = 0x00080000
    val FIELD_IN_ADD_INDEX = 0x00100000
    val FIELD_IS_RENAMED = 0x00200000
    val FIELD_FLAGS_STORAGE_MEDIA = 22
    val FIELD_FLAGS_STORAGE_MEDIA_MASK: Int = 3 << FIELD_FLAGS_STORAGE_MEDIA
    val FIELD_FLAGS_COLUMN_FORMAT = 24
    val FIELD_FLAGS_COLUMN_FORMAT_MASK: Int = 3 << FIELD_FLAGS_COLUMN_FORMAT
    val FIELD_IS_DROPPED = 0x04000000
    val EXPLICIT_NULL_FLAG = 0x08000000
    val FIELD_IS_MARKED = 0x10000000
  }

}