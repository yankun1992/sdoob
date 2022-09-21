package tech.yankun.sdoob.utils

import org.apache.spark.sql.types
import org.apache.spark.sql.types.{DataType, StringType}

import java.sql.JDBCType

object Utils {
  def string2Boolean(string: String): Boolean = string.toLowerCase.trim match {
    case "false" => false
    case "true" => true
    case _ => false
  }

  def jdbcType2sparkType(tp: JDBCType): DataType = {
    return StringType
    tp match {
      case JDBCType.BIT => types.LongType
      case JDBCType.TINYINT => ???
      case JDBCType.SMALLINT => ???
      case JDBCType.INTEGER => ???
      case JDBCType.BIGINT => ???
      case JDBCType.FLOAT => ???
      case JDBCType.REAL => ???
      case JDBCType.DOUBLE => ???
      case JDBCType.NUMERIC => ???
      case JDBCType.DECIMAL => ???
      case JDBCType.CHAR => ???
      case JDBCType.VARCHAR => ???
      case JDBCType.LONGVARCHAR => ???
      case JDBCType.DATE => ???
      case JDBCType.TIME => ???
      case JDBCType.TIMESTAMP => ???
      case JDBCType.BINARY => ???
      case JDBCType.VARBINARY => ???
      case JDBCType.LONGVARBINARY => ???
      case JDBCType.NULL => ???
      case JDBCType.OTHER => ???
      case JDBCType.JAVA_OBJECT => ???
      case JDBCType.DISTINCT => ???
      case JDBCType.STRUCT => ???
      case JDBCType.ARRAY => ???
      case JDBCType.BLOB => ???
      case JDBCType.CLOB => ???
      case JDBCType.REF => ???
      case JDBCType.DATALINK => ???
      case JDBCType.BOOLEAN => ???
      case JDBCType.ROWID => ???
      case JDBCType.NCHAR => ???
      case JDBCType.NVARCHAR => ???
      case JDBCType.LONGNVARCHAR => ???
      case JDBCType.NCLOB => ???
      case JDBCType.SQLXML => ???
      case JDBCType.REF_CURSOR => ???
      case JDBCType.TIME_WITH_TIMEZONE => ???
      case JDBCType.TIMESTAMP_WITH_TIMEZONE => ???
    }
  }
}
