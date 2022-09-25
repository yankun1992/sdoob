/*
 * Copyright (C) 2022  Yan Kun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tech.yankun.sdoob.driver

import io.netty.buffer.{ByteBuf, PooledByteBufAllocator}
import org.log4s.{Logger, getLogger}
import tech.yankun.sdoob.driver.checker.PacketChecker
import tech.yankun.sdoob.driver.codec.CommandCodec
import tech.yankun.sdoob.driver.command.{Command, CommandResponse}

import java.io.Closeable
import java.net.SocketAddress
import java.nio.channels.{SelectableChannel, SocketChannel}
import java.util
import scala.beans.BeanProperty

abstract class Client[P <: ClientPool, CODEC <: CommandCodec[_, _]](val options: SqlConnectOptions, parent: Option[P] = None)
  extends Closeable {

  import Client._

  protected val logger: Logger = getLogger

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

  val socket: SocketChannel = SocketChannel.open()

  val allocator: PooledByteBufAllocator = parent.map(_.alloc).getOrElse(PooledByteBufAllocator.DEFAULT)

  var buffer: ByteBuf = _

  // database codec inflight
  protected val inflight: util.ArrayDeque[CODEC] = new util.ArrayDeque[CODEC]()

  // current running codec
  protected var flightCodec: CODEC = _

  val packetChecker: PacketChecker

  @BeanProperty var bufferRemain: Boolean = false

  protected var inited: Boolean = false

  def currentCodec: CODEC

  def isInit: Boolean = inited

  def init(): Unit

  def configureBlocking(block: Boolean): SelectableChannel = socket.configureBlocking(block)

  def connect(): Unit = {
    // connect to server
    try {
      socket.connect(server)
    } catch {
      case x: Throwable =>
        try socket.close()
        catch {
          case suppressed: Throwable => x.addSuppressed(suppressed)
        }
        throw x
    }

    this.status = Client.ST_CLIENT_CONNECTED
  }


  /**
   * Initialize the configuration after the common configuration have been initialized.
   *
   * @param options the concrete options for initializing configuration by a specific client.
   */
  def initializeConfiguration(options: SqlConnectOptions): Unit

  def channelRead(): Unit

  def write(command: Command): Unit

  def writeable: Boolean = isAuthenticated && inflight.isEmpty

  def sendPacket(packet: ByteBuf): Unit = {
    val len = packet.readableBytes()
    val writeLen = packet.readBytes(socket, len)
    assert(len == writeLen)
  }

  def isSsl: Boolean = false

  def read(): CommandResponse

  def isConnected: Boolean = status == ST_CLIENT_CONNECTED

  def isAuthenticated: Boolean = status == ST_CLIENT_AUTHENTICATED

  def isClosed: Boolean = status == ST_CLIENT_CLOSED

  def wrap(cmd: Command): CODEC

}

object Client {
  val ST_CLIENT_CREATE = 0
  val ST_CLIENT_CONNECTED = 1
  val ST_CLIENT_AUTHENTICATED = 2
  val ST_CLIENT_CLOSED = 3

}
