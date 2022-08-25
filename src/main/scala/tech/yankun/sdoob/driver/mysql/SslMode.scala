package tech.yankun.sdoob.driver.mysql

/**
 * This parameter specifies the desired security state of the connection to the server.
 * More information can be found in <a href="https://dev.mysql.com/doc/refman/8.0/en/connection-options.html#option_general_ssl-mode">MySQL Reference Manual</a>
 *
 * @param value
 */
case class SslMode(value: String)

object SslMode {
  /** establish an unencrypted connection. */
  val DISABLED: SslMode = SslMode("disabled")

  /** establish an encrypted connection if the server supports encrypted connections, falling back to an unencrypted
   * connection if an encrypted connection cannot be established. */
  val PREFERRED: SslMode = SslMode("preferred")

  /** establish an encrypted connection if the server supports encrypted connections. The connection attempt fails
   * if an encrypted connection cannot be established. */
  val REQUIRED: SslMode = SslMode("required")

  /** Like REQUIRED, but additionally verify the server Certificate Authority (CA) certificate against the configured
   * CA certificates. The connection attempt fails if no valid matching CA certificates are found. */
  val VERIFY_CA: SslMode = SslMode("verify_ca")

  /** Like VERIFY_CA, but additionally perform host name identity verification by checking the host name the client
   * uses for connecting to the server against the identity in the certificate that the server sends to the client. */
  val VERIFY_IDENTITY: SslMode = SslMode("verify_identity")

  val VALUES: Array[SslMode] = Array(DISABLED, PREFERRED, REQUIRED, VERIFY_CA, VERIFY_IDENTITY)

  def of(value: String): SslMode = value match {
    case "disabled" => DISABLED
    case "preferred" => PREFERRED
    case "required" => REQUIRED
    case "verify_ca" => VERIFY_CA
    case "verify_identity" => VERIFY_IDENTITY
    case _ => throw new IllegalArgumentException(s"Could not find an appropriate SSL mode for the value [$value]")
  }
}