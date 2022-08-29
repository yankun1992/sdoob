package tech.yankun.sdoob.driver.mysql.protocol

object CommandType {
  /*
   https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_field_list.html
   */
  val COM_QUIT = 0x01
  val COM_INIT_DB = 0x02
  val COM_QUERY = 0x03
  val COM_STATISTICS = 0x09
  val COM_DEBUG = 0x0D
  val COM_PING = 0x0E
  val COM_CHANGE_USER = 0x11
  val COM_RESET_CONNECTION = 0x1F
  val COM_SET_OPTION = 0x1B

  // Prepared Statements
  val COM_STMT_PREPARE = 0x16
  val COM_STMT_EXECUTE = 0x17
  val COM_STMT_FETCH = 0x1C
  val COM_STMT_CLOSE = 0x19
  val COM_STMT_RESET = 0x1A
  val COM_STMT_SEND_LONG_DATA = 0x18

  /*
    Deprecated commands
   */
  @deprecated val COM_FIELD_LIST = 0x04
  @deprecated val COM_REFRESH = 0x07
  @deprecated val COM_PROCESS_INFO = 0x0A
  @deprecated val COM_PROCESS_KILL = 0x0C
}
