package tech.yankun.sdoob.driver.mysql

case class MySQLException(message: String, errorCode: Int, sqlState: String) extends RuntimeException(message)
