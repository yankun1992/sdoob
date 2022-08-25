package tech.yankun.sdoob.net

/**
 * The type of a TCP proxy server.
 */
sealed trait ProxyType

object ProxyType {
  /** HTTP CONNECT ssl proxy */
  object HTTP extends ProxyType

  /** SOCKS4/4a tcp proxy */
  object SOCKS4 extends ProxyType

  /** SOCSK5 tcp proxy */
  object SOCKS5 extends ProxyType
}
