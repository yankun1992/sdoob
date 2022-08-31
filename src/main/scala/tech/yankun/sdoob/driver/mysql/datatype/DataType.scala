package tech.yankun.sdoob.driver.mysql.datatype

import io.netty.buffer.ByteBuf
import org.log4s._
import tech.yankun.sdoob.driver.mysql.data.spatial.Geometry
import tech.yankun.sdoob.driver.mysql.protocol.ColumnDefinition

import java.sql.JDBCType
import java.time.{Duration, LocalDate, LocalDateTime}

case class DataType(
                     id: Int,
                     binaryType: Class[_],
                     textType: Class[_],
                     jdbcType: JDBCType
                   )


object DataType {

  val INT1: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_TINY, classOf[Byte], classOf[Byte], JDBCType.TINYINT)
  val INT2: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_SHORT, classOf[Short], classOf[Short], JDBCType.SMALLINT)
  val INT3: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_INT24, classOf[Integer], classOf[Integer], JDBCType.INTEGER)
  val INT4: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_LONG, classOf[Integer], classOf[Integer], JDBCType.INTEGER)
  val INT8: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_LONGLONG, classOf[Long], classOf[Long], JDBCType.BIGINT)
  val DOUBLE: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_DOUBLE, classOf[Double], classOf[Double], JDBCType.DOUBLE)
  val FLOAT: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_FLOAT, classOf[Float], classOf[Float], JDBCType.REAL)
  val NUMERIC: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_NEWDECIMAL, classOf[Numeric[_]], classOf[Numeric[_]], JDBCType.DECIMAL)
  val STRING: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_STRING, classOf[ByteBuf], classOf[String], JDBCType.VARCHAR)
  val VARSTRING: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_VAR_STRING, classOf[ByteBuf], classOf[String], JDBCType.VARCHAR)
  val TINYBLOB: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_TINY_BLOB, classOf[ByteBuf], classOf[String], JDBCType.BLOB)
  val BLOB: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_BLOB, classOf[ByteBuf], classOf[String], JDBCType.BLOB)
  val MEDIUMBLOB: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_MEDIUM_BLOB, classOf[ByteBuf], classOf[String], JDBCType.BLOB)
  val LONGBLOB: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_LONG_BLOB, classOf[ByteBuf], classOf[String], JDBCType.BLOB)
  val DATE: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_DATE, classOf[LocalDate], classOf[LocalDate], JDBCType.DATE)
  val TIME: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_TIME, classOf[Duration], classOf[Duration], JDBCType.TIME)
  val DATETIME: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_DATETIME, classOf[LocalDateTime], classOf[LocalDateTime], JDBCType.TIMESTAMP)
  val YEAR: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_YEAR, classOf[Short], classOf[Short], JDBCType.SMALLINT)
  val TIMESTAMP: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_TIMESTAMP, classOf[LocalDateTime], classOf[LocalDateTime], JDBCType.TIMESTAMP)
  val BIT: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_BIT, classOf[Long], classOf[Long], JDBCType.BIT)
  val JSON: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_JSON, classOf[Object], classOf[Object], JDBCType.OTHER)
  val GEOMETRY: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_GEOMETRY, classOf[Geometry], classOf[Geometry], JDBCType.OTHER)
  val NULL: DataType = DataType(ColumnDefinition.ColumnType.MYSQL_TYPE_NULL, classOf[Object], classOf[Object], JDBCType.OTHER)
  val UNBIND: DataType = DataType(-1, classOf[Object], classOf[Object], JDBCType.OTHER)


  private[this] val logger = getLogger
  private val idToDataType = collection.mutable.Map.empty[Int, DataType]

  def valueOf(value: Int): DataType = {
    idToDataType.get(value) match {
      case Some(value) => value
      case None =>
        logger.warn(s"MySQL data type Id =[${value}] not handled - using string type instead")
        STRING
    }
  }
}