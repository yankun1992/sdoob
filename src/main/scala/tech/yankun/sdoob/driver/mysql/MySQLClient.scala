package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.{ByteBuf, PooledByteBufAllocator}
import org.log4s.getLogger
import tech.yankun.sdoob.driver.command._
import tech.yankun.sdoob.driver.mysql.codec._
import tech.yankun.sdoob.driver.mysql.command.InitialHandshakeCommand
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag.{CLIENT_CONNECT_ATTRS, CLIENT_CONNECT_WITH_DB, CLIENT_FOUND_ROWS, CLIENT_SUPPORTED_CAPABILITIES_FLAGS}
import tech.yankun.sdoob.driver.{Client, SqlConnectOptions}

import java.net.SocketException
import java.nio.channels.{SelectableChannel, SocketChannel}
import java.nio.charset.Charset
import java.util
import scala.beans.BeanProperty

class MySQLClient(options: MySQLConnectOptions, parent: Option[MySQLPool] = None) extends Client(options) {

  import MySQLClient._

  private[this] val logger = getLogger

  private var collation: MySQLCollation = _
  private var charsetEncoding: Charset = _
  private var useAffectedRows: Boolean = _
  //  private var sslMode: SslMode = _
  private var serverRsaPublicKey: ByteBuf = null

  private var initialCapabilitiesFlags: Int = initCapabilitiesFlags(options.getDatabase)

  //  private var pipeliningLimit: Int = options.getPipeliningLimit

  // mysql codec inflight
  private val inflight: util.ArrayDeque[CommandCodec[_, MySQLClient]] = new util.ArrayDeque[CommandCodec[_, MySQLClient]]()

  // current running codec
  private var flightCodec: CommandCodec[_, MySQLClient] = null

  var clientCapabilitiesFlag: Int = _
  var encodingCharset: Charset = _

  var metadata: MySQLDatabaseMetadata = _

  val allocator: PooledByteBufAllocator = parent.map(_.alloc).getOrElse(PooledByteBufAllocator.DEFAULT)
  var buffer: ByteBuf = _

  @BeanProperty var bufferRemain: Boolean = false

  val socket: SocketChannel = SocketChannel.open()

  var inited: Boolean = false

  private var pauseCodec: Boolean = false

  def setPauseCodec(status: Boolean): Unit = pauseCodec = status

  def clientIsPause: Boolean = pauseCodec

  def currentCodec: CommandCodec[_, MySQLClient] = flightCodec

  def codecCompleted: Boolean = inflight.isEmpty || isClosed

  def isInit: Boolean = inited

  def connect(): Unit = {
    // connect to server
    try {
      socket.connect(server)
    } catch {
      case x: Throwable =>
        try socket.close()
        catch {
          case suppressed: Throwable =>
            x.addSuppressed(suppressed)
        }
        throw x
    }

    this.status = Client.ST_CLIENT_CONNECTED
  }

  def init(): Unit = {
    sendStartupMessage(options.getUser, options.getPassword, options.getDatabase, collation,
      options.serverRsaPublicKeyValue, options.getProperties, options.getSslMode, initialCapabilitiesFlags,
      charsetEncoding, options.getAuthenticationPlugin
    )
    inited = true
    logger.info(s"client[${clientId}] send startup message")
  }

  def configureBlocking(block: Boolean): SelectableChannel = socket.configureBlocking(block)

  override def initializeConfiguration(options: SqlConnectOptions): Unit = {
    val myOptions = options.asInstanceOf[MySQLConnectOptions]
    if (myOptions.getCollation != null) {
      collation = MySQLCollation.valueOfName(myOptions.getCollation)
      charsetEncoding = Charset.forName(collation.mappedJavaCharsetName)
    } else {
      if (myOptions.getCharset == null) {
        collation = MySQLCollation.DEFAULT_COLLATION
      } else {
        collation = MySQLCollation.valueOfName(MySQLCollation.getDefaultCollationFromCharsetName(myOptions.getCharset))
      }
      if (myOptions.getCharacterEncoding == null) {
        charsetEncoding = Charset.defaultCharset()
      } else {
        charsetEncoding = Charset.forName(myOptions.getCharacterEncoding)
      }
    }
  }


  private def sendStartupMessage(username: String, password: String, database: String, collation: MySQLCollation,
                                 serverRsaPublicKey: ByteBuf, properties: Map[String, String], sslMode: SslMode,
                                 initialCapabilitiesFlags: Int, charsetEncoding: Charset,
                                 authenticationPlugin: MySQLAuthenticationPlugin): Unit = {
    val cmd = new InitialHandshakeCommand(this, username, password, database, collation, serverRsaPublicKey,
      properties, sslMode, initialCapabilitiesFlags, charsetEncoding, authenticationPlugin)
    write(cmd)
  }

  override def write(command: Command): Unit = {
    val codec: CommandCodec[_, MySQLClient] = wrap(command)
    if (inflight.isEmpty) flightCodec = codec
    inflight.addLast(codec)
    codec.encode(this)
  }

  def writeable: Boolean = isAuthenticated && inflight.isEmpty

  def handleCommandResponse(res: AnyRef): Unit = {
    val codec = inflight.poll()
    if (!inflight.isEmpty) flightCodec = inflight.peek() else flightCodec = null
    res match {
      case exception: Exception =>
        throw exception
      case _ =>
        logger.info(s"Command[${codec.cmd}] response is ${res}")
    }
  }

  override def sendPacket(packet: ByteBuf): Unit = {
    val len = packet.readableBytes()
    val writeLen = packet.readBytes(socket, len)
    assert(len == writeLen)
  }

  def readChannel(): Unit = {
    val buf = getByteBuf(true)
    val writableBytes = buf.writableBytes()
    val read = buf.writeBytes(socket, writableBytes)
    if (read == -1) throw new SocketException(s"client[${clientId}] socket has closed")
    logger.info(s"read channel $read bytes")
    while (decode(buf) && !pauseCodec) {}
  }

  def getByteBuf(inRead: Boolean = false): ByteBuf = {
    if (bufferRemain && inRead) {
      buffer
    } else if (inRead) {
      //      assert(this.buffer == null)
      this.buffer = allocator.directBuffer(BUFFER_SIZE)
      this.buffer
    } else {
      allocator.directBuffer(BUFFER_SIZE)
    }
  }

  def release(buffer: ByteBuf): Unit = {
    if (buffer != this.buffer) {
      buffer.release()
    } else if (bufferRemain) {
      //      this.buffer.discardReadBytes()
    } else if (!bufferRemain && parent.isEmpty) {
      this.buffer.clear()
      bufferRemain = true
    } else {
      this.buffer = null
      buffer.release()
    }
  }

  private def discard(): Unit = if (this.buffer != null) {
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

  protected def decode(in: ByteBuf): Boolean = if (in.readableBytes() > 4) {
    val packetStart = in.readerIndex()
    val length = in.readUnsignedMediumLE()
    val sequenceId: Int = in.readUnsignedByte()

    if (in.readableBytes() == length) {
      //      logger.info(s"client[${clientId}] decode payload with ${length} len")
      bufferRemain = false
      decodePacket(in, length, sequenceId)
      false
    } else if (in.readableBytes() > length) {
      bufferRemain = true
      this.buffer = in
      //      logger.info(s"client[${clientId}] packet remain len ${in.readableBytes() - length}, current decode ${length} payload")
      decodePacket(in, length, sequenceId)
      in.readerIndex(packetStart + 4 + length)
      true
    } else {
      bufferRemain = true
      this.buffer = in
      in.readerIndex(packetStart)
      discard()
      false
    }
  } else {
    bufferRemain = true
    this.buffer = in
    discard()
    false
  }

  private def decodePacket(payload: ByteBuf, length: Int, sequenceId: Int): Unit = {
    val codec = inflight.peek()
    flightCodec = codec
    codec.sequenceId = sequenceId + 1
    codec.decodePayload(payload, length)
  }

  override def read(): CommandResponse = ???


  override def close(): Unit = ???

  private def initCapabilitiesFlags(database: String): Int = {
    var flags = CLIENT_SUPPORTED_CAPABILITIES_FLAGS
    if (database != null && database.nonEmpty) {
      flags |= CLIENT_CONNECT_WITH_DB
    }
    if (properties.nonEmpty) {
      flags |= CLIENT_CONNECT_ATTRS
    }
    if (!useAffectedRows) {
      flags |= CLIENT_FOUND_ROWS
    }
    flags
  }

}

object MySQLClient {
  protected val BUFFER_SIZE: Int = 8 * 1024
  protected val BIG_BUFFER_SIZE: Int = 128 * 1024

  def wrap(cmd: Command): CommandCodec[_, MySQLClient] = {
    cmd match {
      case command: InitialHandshakeCommand =>
        new InitialHandshakeCommandCodec(command)
      case command: SimpleQueryCommand =>
        new SimpleQueryCommandCodec(command)
      case command: SdoobSimpleQueryCommand => new SdoobSimpleQueryCommandCodec(command)
      case CloseConnectionCommand => new CloseConnectionCommandCodec(CloseConnectionCommand)
      case _ =>
        ???
    }
  }


}
