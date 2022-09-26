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
import tech.yankun.sdoob.driver.checker.PacketChecker._
import tech.yankun.sdoob.driver.codec.CommandCodec
import tech.yankun.sdoob.driver.command.{Command, CommandResponse}

import java.io.Closeable
import java.net.{SocketAddress, SocketException}
import java.nio.channels.{SelectableChannel, SocketChannel}
import java.util
import scala.beans.BeanProperty

abstract class Client[P <: ClientPool, CODEC <: CommandCodec[_ <: Command, _]](val options: SqlConnectOptions, parent: Option[P] = None)
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
  protected var flightCodec: Option[CODEC] = None

  val packetChecker: PacketChecker

  @BeanProperty var bufferRemain: Boolean = false

  protected var inited: Boolean = false

  def codecCompleted: Boolean = inflight.isEmpty || isClosed

  def currentCodec: CODEC = flightCodec.get

  def isInit: Boolean = inited

  def init(): Unit = inited = true

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

  def channelRead(): Unit = {
    val buf = getByteBuf(true)
    val writableBytes = buf.writableBytes()
    val read = buf.writeBytes(socket, writableBytes)
    if (read == -1) throw new SocketException(s"client[${clientId}] socket has closed")
    var bufferState: PacketState = packetChecker.check(buf)
    while (bufferState != NO_FULL_PACKET && bufferState != NO_PACKET) {
      bufferState match {
        case ONLY_ONE_PACKET =>
          bufferRemain = false
          decodePacket(buf)
          bufferState = NO_PACKET
        case MORE_THAN_ONE_PACKET =>
          bufferRemain = true
          this.buffer = buf
          decodePacket(buf)
          bufferState = packetChecker.check(buf)
      }
    }
    if (bufferState == NO_FULL_PACKET) {
      bufferRemain = true
      this.buffer = buf
      discard()
    }
  }

  protected def decodePacket(packet: ByteBuf): Unit

  def write(command: Command): Unit

  def writeable: Boolean = isAuthenticated && inflight.isEmpty

  def sendPacket(packet: ByteBuf): Unit = {
    val len = packet.readableBytes()
    val writeLen = packet.readBytes(socket, len)
    assert(len == writeLen)
  }

  def handleCommandResponse(res: AnyRef): Unit = {
    val codec = inflight.poll()
    if (!inflight.isEmpty) flightCodec = Some(inflight.peek()) else flightCodec = None
    res match {
      case exception: Exception =>
        throw exception
      case _ =>
        logger.info(s"Command[${codec.cmd}] response is ${res}")
    }
  }

  def isSsl: Boolean = false

  def read(): CommandResponse

  def isConnected: Boolean = status == ST_CLIENT_CONNECTED

  def isAuthenticated: Boolean = status == ST_CLIENT_AUTHENTICATED

  def isClosed: Boolean = status == ST_CLIENT_CLOSED

  protected def wrap(cmd: Command): CODEC

  def getByteBuf(inRead: Boolean = false): ByteBuf = {
    if (bufferRemain && inRead) {
      buffer
    } else if (inRead) {
      this.buffer = allocator.directBuffer(BUFFER_SIZE)
      this.buffer
    } else if (!inRead && bufferRemain && this.buffer.readableBytes() == 0) {
      this.buffer
    } else {
      allocator.directBuffer(BUFFER_SIZE)
    }
  }

  def release(buffer: ByteBuf): Unit = {
    if (buffer != this.buffer) {
      buffer.release()
    } else if (bufferRemain) {
      if (buffer.readableBytes() == 0) buffer.clear()
    } else if (!bufferRemain && parent.isEmpty) {
      this.buffer.clear()
      bufferRemain = true
    } else {
      this.buffer = null
      buffer.release()
    }
  }

  protected def discard(): Unit = if (this.buffer != null) {
    if (this.buffer.readerIndex() > BUFFER_SIZE * 3.0 / 4.0) this.buffer.discardReadBytes()
    else if (!this.buffer.isWritable && this.buffer.readerIndex() > 0) this.buffer.discardReadBytes()
    else if (this.buffer.readerIndex() == 0 && !this.buffer.isWritable) {
      // big payload , change buffer
      val buffer = this.buffer
      this.buffer = allocator.directBuffer(BIG_BUFFER_SIZE)
      this.buffer.writeBytes(buffer)
      buffer.release()
      logger.warn("upgrade buffer to big buffer")
    }
  }

}

object Client {
  val ST_CLIENT_CREATE = 0
  val ST_CLIENT_CONNECTED = 1
  val ST_CLIENT_AUTHENTICATED = 2
  val ST_CLIENT_CLOSED = 3

  protected val BUFFER_SIZE: Int = 8 * 1024
  protected val BIG_BUFFER_SIZE: Int = 128 * 1024

}
