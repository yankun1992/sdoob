package tech.yankun.sdoob.driver

import tech.yankun.sdoob.net.ProxyOptions

import java.net.{InetSocketAddress, SocketAddress}
import scala.beans.BeanProperty
import scala.collection.mutable

class SqlConnectOptions() extends Serializable {

  import SqlConnectOptions._

  // client options
  @BeanProperty var connectTimeout: Int = DEFAULT_CONNECT_TIMEOUT
  var trustAll: Boolean = DEFAULT_TRUST_ALL
  var metricsName: String = DEFAULT_METRICS_NAME
  var proxyOptions: ProxyOptions = _
  var localAddress: String = _
  var nonProxyHosts: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty[String]

  // network options
  private var reconnectAttempts: Int = DEFAULT_RECONNECT_ATTEMPTS
  private var reconnectInterval: Long = DEFAULT_RECONNECT_INTERVAL
  @BeanProperty var hostnameVerificationAlgorithm: String = DEFAULT_HOSTNAME_VERIFICATION_ALGORITHM
  @BeanProperty var applicationLayerProtocols: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty[String]

  // database options
  @BeanProperty var host: String = _
  @BeanProperty var port: Int = _
  @BeanProperty var user: String = _
  @BeanProperty var password: String = _
  @BeanProperty var database: String = _
  @BeanProperty var cachePreparedStatements: Boolean = DEFAULT_CACHE_PREPARED_STATEMENTS
  @BeanProperty var preparedStatementCacheMaxSize: Int = DEFAULT_PREPARED_STATEMENT_CACHE_MAX_SIZE

  @BeanProperty var preparedStatementCacheSqlFilter: String => Boolean = (sql: String) => sql.length < DEFAULT_PREPARED_STATEMENT_CACHE_SQL_LIMIT

  var properties = new mutable.HashMap[String, String]()

  /**
   * Copy constructor
   *
   * @param other the options to copy
   */
  def this(other: SqlConnectOptions) {
    this()
    init(other)
  }

  protected def init(other: SqlConnectOptions): Unit = {
    // client options

    // network options
    reconnectAttempts = other.reconnectAttempts
    reconnectInterval = other.reconnectInterval
    hostnameVerificationAlgorithm = other.hostnameVerificationAlgorithm
    other.applicationLayerProtocols.foreach(protocol => applicationLayerProtocols.append(protocol))


    // database options
    host = other.host
    port = other.port
    user = other.user
    password = other.password
    database = other.database
    cachePreparedStatements = other.cachePreparedStatements
    preparedStatementCacheMaxSize = other.preparedStatementCacheMaxSize
    preparedStatementCacheSqlFilter = other.preparedStatementCacheSqlFilter
    if (other.properties != null) {
      for ((key, value) <- other.properties) {
        properties.put(key, value)
      }
    }
  }

  /**
   * Set the value of reconnect attempts
   *
   * @param attempts the maximum number of reconnect attempts
   * @return a reference to this, so the API can be used fluently
   */
  def setReconnectAttempts(attempts: Int): this.type = {
    if (attempts < -1) {
      throw new IllegalArgumentException("reconnect attempts must be >= -1")
    }
    this.reconnectAttempts = attempts
    this
  }

  def getReconnectAttempts: Int = this.reconnectAttempts

  /**
   * Set the reconnect interval
   *
   * @param interval the reconnect interval in ms
   * @return a reference to this, so the API can be used fluently
   */
  def setReconnectInterval(interval: Long): this.type = {
    if (interval < -1) {
      throw new IllegalArgumentException("reconnect interval must be >= -1")
    }
    reconnectInterval = interval
    this
  }

  def getReconnectInterval: Long = this.reconnectInterval

  def getProperties: Map[String, String] = this.properties.toMap

  def addProperty(key: String, value: String): this.type = {
    properties.put(key, value)
    this
  }

  def addProperty(others: Map[String, String]): this.type = {
    others.foreach { case (k, v) => addProperty(k, v) }
    this
  }

  def getSocketAddress: SocketAddress = {
    new InetSocketAddress(getHost, getPort)
  }
}


object SqlConnectOptions {
  // client options default
  /** The default value of connect timeout = 60000 ms */
  val DEFAULT_CONNECT_TIMEOUT: Int = 60000
  /** The default value of whether all servers (SSL/TLS) should be trusted = false */
  val DEFAULT_TRUST_ALL: Boolean = false
  /** The default value of the client metrics = "": */
  val DEFAULT_METRICS_NAME: String = ""

  // network options default
  /** The default value for reconnect attempts = 0 */
  val DEFAULT_RECONNECT_ATTEMPTS: Int = 0
  /** The default value for reconnect interval = 1000 ms */
  val DEFAULT_RECONNECT_INTERVAL: Long = 1000
  /** Default value to determine hostname verification algorithm hostname verification (for SSL/TLS) = "" */
  val DEFAULT_HOSTNAME_VERIFICATION_ALGORITHM: String = ""

  // database options default
  val DEFAULT_CACHE_PREPARED_STATEMENTS: Boolean = false
  val DEFAULT_PREPARED_STATEMENT_CACHE_MAX_SIZE: Int = 256
  val DEFAULT_PREPARED_STATEMENT_CACHE_SQL_LIMIT: Int = 2048

}