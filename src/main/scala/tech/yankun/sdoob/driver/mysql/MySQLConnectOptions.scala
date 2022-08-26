package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.SqlConnectOptions

import scala.beans.BeanProperty

/**
 * Connect options for configuring [[MySQLClient]].
 */
class MySQLConnectOptions extends SqlConnectOptions {

  import MySQLConnectOptions._

  @BeanProperty var collation: String = _
  @BeanProperty var charset: String = DEFAULT_CHARSET
  @BeanProperty var useAffectedRows: Boolean = DEFAULT_USE_AFFECTED_ROWS
  @BeanProperty var sslMode: SslMode = DEFAULT_SSL_MODE
  @BeanProperty var serverRsaPublicKeyPath: String = _
  @BeanProperty var serverRsaPublicKeyValue: ByteBuf = _
  @BeanProperty var characterEncoding: String = DEFAULT_CHARACTER_ENCODING
  private var pipeliningLimit: Int = DEFAULT_PIPELINING_LIMIT
  @BeanProperty var authenticationPlugin: MySQLAuthenticationPlugin = MySQLAuthenticationPlugin.DEFAULT

  // override
  host = DEFAULT_HOST
  port = DEFAULT_PORT
  user = DEFAULT_USER
  password = DEFAULT_PASSWORD
  database = DEFAULT_SCHEMA
  addProperty(DEFAULT_CONNECTION_ATTRIBUTES)

  def this(other: SqlConnectOptions) {
    this()
    this.init(other)
  }

  override protected def init(other: SqlConnectOptions): Unit = {
    super.init(other)
    if (other.isInstanceOf[MySQLConnectOptions]) {
      val oth = other.asInstanceOf[MySQLConnectOptions]
      collation = oth.collation
      charset = oth.charset
      useAffectedRows = oth.useAffectedRows
      sslMode = oth.sslMode
      serverRsaPublicKeyPath = oth.serverRsaPublicKeyPath
      serverRsaPublicKeyValue = oth.serverRsaPublicKeyValue
      characterEncoding = oth.characterEncoding
      authenticationPlugin = oth.authenticationPlugin
    }
  }

  /**
   * Set the pipelining limit count.
   *
   * @param pipeliningLimit the count to configure
   * @return a reference to this, so the API can be used fluently
   */
  def setPipeliningLimit(pipeliningLimit: Int): this.type = {
    if (pipeliningLimit < 1) {
      throw new IllegalArgumentException("pipelining limit can not be less than 1")
    }
    this.pipeliningLimit = pipeliningLimit
    this
  }

  def getPipeliningLimit: Int = this.pipeliningLimit

}

object MySQLConnectOptions {
  def wrap(options: SqlConnectOptions): MySQLConnectOptions = {
    new MySQLConnectOptions(options)
  }

  def fromUri(uri: String): MySQLConnectOptions = {
    MySQLUriParser.parse(uri)
  }

  val DEFAULT_HOST: String = "localhost"
  val DEFAULT_PORT: Int = 3306
  val DEFAULT_USER: String = "root"
  val DEFAULT_PASSWORD: String = ""
  val DEFAULT_SCHEMA: String = ""
  val DEFAULT_CHARSET: String = "utf8mb4"
  val DEFAULT_USE_AFFECTED_ROWS: Boolean = false
  val DEFAULT_CONNECTION_ATTRIBUTES: Map[String, String] = Map("_client_name" -> "sdoob-mysql-client")
  val DEFAULT_SSL_MODE: SslMode = SslMode.DISABLED
  val DEFAULT_CHARACTER_ENCODING: String = "UTF-8"
  val DEFAULT_PIPELINING_LIMIT: Int = 1


}