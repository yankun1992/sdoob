package tech.yankun.sdoob.net


import scala.beans.BeanProperty

/**
 * Proxy options for a net client or a net client.
 */
class ProxyOptions {

  import ProxyOptions._

  @BeanProperty var host: String = DEFAULT_HOST
  @BeanProperty var port: Int = DEFAULT_PORT
  @BeanProperty var username: String = _
  @BeanProperty var password: String = _

  @BeanProperty var `type`: ProxyType = DEFAULT_TYPE

  /**
   * Copy constructor.
   *
   * @param other the options to copy
   */
  def this(other: ProxyOptions) {
    this()
    host = other.getHost
    port = other.getPort
    username = other.getUsername
    password = other.getPassword
    `type` = other.getType
  }

}

object ProxyOptions {
  /** The default proxy type (HTTP) */
  val DEFAULT_TYPE: ProxyType = ProxyType.HTTP

  /** The default port for proxy connect = 3128
   * 3128 is the default port for e.g. Squid */
  val DEFAULT_PORT: Int = 3128

  /** The default hostname for proxy connect = "localhost" */
  val DEFAULT_HOST: String = "localhost"

}