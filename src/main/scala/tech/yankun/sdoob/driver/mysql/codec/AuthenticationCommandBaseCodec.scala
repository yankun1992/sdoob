package tech.yankun.sdoob.driver.mysql.codec

import tech.yankun.sdoob.driver.Command
import tech.yankun.sdoob.driver.mysql.MySQLClient

abstract class AuthenticationCommandBaseCodec[C <: Command](cmd: C) extends CommandCodec[C, MySQLClient](cmd) {
  protected var authPluginData: Array[Byte] = _
  private var isWaitingForRsaPublicKey: Boolean = false

  // TODO

}

object AuthenticationCommandBaseCodec {
   val NONCE_LENGTH = 20
  protected val AUTH_SWITCH_REQUEST_STATUS_FLAG = 0xFE

  protected val AUTH_MORE_DATA_STATUS_FLAG = 0x01
  protected val AUTH_PUBLIC_KEY_REQUEST_FLAG = 0x02
  protected val FAST_AUTH_STATUS_FLAG = 0x03
  protected val FULL_AUTHENTICATION_STATUS_FLAG = 0x04
}
