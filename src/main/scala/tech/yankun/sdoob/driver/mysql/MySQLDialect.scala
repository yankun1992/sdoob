package tech.yankun.sdoob.driver.mysql

import org.apache.spark.sql.types.DataType
import tech.yankun.sdoob.driver.Dialect

object MySQLDialect extends Dialect {
  override def getSparkType(SQLType: Int, typeName: String, size: Int): Option[DataType] = None
}
