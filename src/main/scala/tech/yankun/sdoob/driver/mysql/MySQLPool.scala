package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.PooledByteBufAllocator
import org.log4s.getLogger
import tech.yankun.sdoob.driver.{ClientPool, PoolOptions}

import java.nio.channels.{SelectionKey, Selector}

class MySQLPool(options: PoolOptions, connectOptions: MySQLConnectOptions) extends ClientPool {

  private[this] val logger = getLogger

  private val allocator: PooledByteBufAllocator = PooledByteBufAllocator.DEFAULT

  def alloc: PooledByteBufAllocator = allocator

  private val selector = Selector.open()
  val clients = (1 to options.getSize).map { idx =>
    val client = new MySQLClient(connectOptions)
    client.setClientId(idx)
    // connect and send auth message
    client.connect()
    client.configureBlocking(false)
    client.socket.register(selector, SelectionKey.OP_READ, client)

    (idx, client)
  }.toMap

  val connectedClients = collection.mutable.HashMap.empty[Int, MySQLClient]
  var times = 1
  while (connectedClients.size < options.getSize && times < 20) {
    logger.info(s"start while ${times} times")
    selector.select(1000)
    val keys = selector.selectedKeys().iterator()
    while (keys.hasNext) {
      val key = keys.next()
      keys.remove()
      if (key.isReadable) {
        val client = key.attachment().asInstanceOf[MySQLClient]
        assert(client.isConnected)
        if (!client.isInit) {
          client.init()
        }
        client.channelRead()
        if (client.isAuthenticated) {
          connectedClients.put(client.getClientId, client)
        }
      }
    }
    times += 1
    logger.info("end while")
  }

  def getReadyClient(): Option[MySQLClient] = {
    parseResult()
    connectedClients.find { case (idx, client) =>
      client.writeable
    }.map(_._2)
  }

  def parseResult(): Unit = {
    var existsWriteable = connectedClients.exists(_._2.writeable)
    while (!existsWriteable) {
      selector.select(1000)
      val keys = selector.selectedKeys().iterator()
      while (keys.hasNext) {
        val key = keys.next()
        keys.remove()
        if (key.isReadable) {
          val client = key.attachment().asInstanceOf[MySQLClient]
          client.channelRead()
          if (client.writeable) {
            existsWriteable = true
          }
        }
      }
    }
  }


}


object MySQLPool {
  def apply(options: PoolOptions, connectOptions: MySQLConnectOptions): MySQLPool = new MySQLPool(options, connectOptions)

  def create(options: PoolOptions, connectOptions: MySQLConnectOptions): MySQLPool = new MySQLPool(options, connectOptions)

  def create(options: PoolOptions, uri: String): MySQLPool = {
    val connectOptions = MySQLConnectOptions.fromUri(uri)
    new MySQLPool(options, connectOptions)
  }
}
