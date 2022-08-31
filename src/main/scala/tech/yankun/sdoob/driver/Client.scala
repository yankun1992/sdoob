package tech.yankun.sdoob.driver

import java.io.Closeable
import java.net.SocketAddress

abstract class Client(val options: SqlConnectOptions) extends Closeable {
  val properties: Map[String, String] = options.getProperties
  val server: SocketAddress = options.getSocketAddress
  val user: String = options.getUser()
  val password: String = options.getPassword()
  val database: String = options.getDatabase()

  val cachePreparedStatements: Boolean = options.getCachePreparedStatements()
  val preparedStatementCacheSize: Int = options.getPreparedStatementCacheMaxSize()
  val preparedStatementCacheSqlFilter: String => Boolean = options.getPreparedStatementCacheSqlFilter()

  initializeConfiguration(options)


  /**
   * Initialize the configuration after the common configuration have been initialized.
   *
   * @param options the concrete options for initializing configuration by a specific client.
   */
  def initializeConfiguration(options: SqlConnectOptions): Unit


  def write(command: Command): Unit

  def read(): CommandResponse
}
