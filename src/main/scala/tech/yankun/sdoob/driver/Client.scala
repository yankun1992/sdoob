package tech.yankun.sdoob.driver

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.{Command, CommandResponse}

import java.io.Closeable
import java.net.SocketAddress
import scala.beans.BeanProperty

abstract class Client(val options: SqlConnectOptions) extends Closeable {

  import Client._

  val properties: Map[String, String] = options.getProperties
  val server: SocketAddress = options.getSocketAddress
  val user: String = options.getUser()
  val password: String = options.getPassword()
  val database: String = options.getDatabase()

  val cachePreparedStatements: Boolean = options.getCachePreparedStatements()
  val preparedStatementCacheSize: Int = options.getPreparedStatementCacheMaxSize()
  val preparedStatementCacheSqlFilter: String => Boolean = options.getPreparedStatementCacheSqlFilter()

  @BeanProperty var status: Int = ST_CLIENT_CREATE

  @BeanProperty var clientId: Int = _

  initializeConfiguration(options)


  /**
   * Initialize the configuration after the common configuration have been initialized.
   *
   * @param options the concrete options for initializing configuration by a specific client.
   */
  def initializeConfiguration(options: SqlConnectOptions): Unit


  def write(command: Command): Unit

  def sendPacket(packet: ByteBuf): Unit

  def isSsl: Boolean = false

  def read(): CommandResponse

  def isConnected: Boolean = status == ST_CLIENT_CONNECTED

  def isAuthenticated: Boolean = status == ST_CLIENT_AUTHENTICATED

  def isClosed: Boolean = status == ST_CLIENT_CLOSED

}

object Client {
  val ST_CLIENT_CREATE = 0
  val ST_CLIENT_CONNECTED = 1
  val ST_CLIENT_AUTHENTICATED = 2
  val ST_CLIENT_CLOSED = 3

}
