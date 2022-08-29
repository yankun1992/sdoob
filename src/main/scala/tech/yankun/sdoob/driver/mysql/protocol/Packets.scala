package tech.yankun.sdoob.driver.mysql.protocol

/**
 * MySQL Packets.
 */
object Packets {
  val OK_PACKET_HEADER = 0x00
  val EOF_PACKET_HEADER = 0xFE
  val ERROR_PACKET_HEADER = 0xFF
  val PACKET_PAYLOAD_LENGTH_LIMIT = 0xFFFFFF

  case class OkPacket(
                       affectedRows: Long,
                       lastInsertId: Long,
                       serverStatusFlags: Int,
                       numberOfWarnings: Int,
                       statusInfo: String,
                       sessionStateInfo: String
                     )

  case class EofPacket(
                        numberOfWarnings: Int,
                        serverStatusFlags: Int
                      )

  object ServerStatusFlags {
    /*
      https://dev.mysql.com/doc/dev/mysql-server/latest/mysql__com_8h.html#a1d854e841086925be1883e4d7b4e8cad
     */
    val SERVER_STATUS_IN_TRANS: Int = 0x0001
    val SERVER_STATUS_AUTOCOMMIT: Int = 0x0002
    val SERVER_MORE_RESULTS_EXISTS: Int = 0x0008
    val SERVER_STATUS_NO_GOOD_INDEX_USED: Int = 0x0010
    val SERVER_STATUS_NO_INDEX_USED: Int = 0x0020
    val SERVER_STATUS_CURSOR_EXISTS: Int = 0x0040
    val SERVER_STATUS_LAST_ROW_SENT: Int = 0x0080
    val SERVER_STATUS_DB_DROPPED: Int = 0x0100
    val SERVER_STATUS_NO_BACKSLASH_ESCAPES: Int = 0x0200
    val SERVER_STATUS_METADATA_CHANGED: Int = 0x0400
    val SERVER_QUERY_WAS_SLOW: Int = 0x0800
    val SERVER_PS_OUT_PARAMS: Int = 0x1000
    val SERVER_STATUS_IN_TRANS_READONLY: Int = 0x2000
    val SERVER_SESSION_STATE_CHANGED: Int = 0x4000

  }

  object EnumCursorType {
    val CURSOR_TYPE_NO_CURSOR = 0
    val CURSOR_TYPE_READ_ONLY = 1

    // not supported by the server for now
    val CURSOR_TYPE_FOR_UPDATE = 2
    val CURSOR_TYPE_SCROLLABLE = 4
  }

  object ParameterFlag {
    val UNSIGNED = 0x80
  }
}