package tech.yankun.sdoob.driver.mysql

/**
 * MySQL authentication plugins which can be specified at the connection start, more information could be found
 * in <a href="https://dev.mysql.com/doc/refman/8.0/en/authentication-plugins.html">MySQL Reference Manual</a>.
 *
 * @param value
 */
case class MySQLAuthenticationPlugin(value: String)

object MySQLAuthenticationPlugin {
  /** Default authentication plugin, the client will firstly try to use the plugin name provided by the server. */
  val DEFAULT: MySQLAuthenticationPlugin = MySQLAuthenticationPlugin("")

  /** Authentication plugin which enables the client to send password to the server as cleartext without encryption. */
  val MYSQL_CLEAR_PASSWORD: MySQLAuthenticationPlugin = MySQLAuthenticationPlugin("mysql_clear_password")

  /** Authentication plugin which uses SHA-1 hash function to scramble the password and send it to the server. */
  val MYSQL_NATIVE_PASSWORD: MySQLAuthenticationPlugin = MySQLAuthenticationPlugin("mysql_native_password")

  /** Authentication plugin which uses SHA-256 hash function to scramble the password and send it to the server. */
  val SHA256_PASSWORD: MySQLAuthenticationPlugin = MySQLAuthenticationPlugin("sha256_password")

  /** Like [[SHA256_PASSWORD]] but enables caching on the server side for better performance and with wider applicability. */
  val CACHING_SHA2_PASSWORD: MySQLAuthenticationPlugin = MySQLAuthenticationPlugin("caching_sha2_password")
}
