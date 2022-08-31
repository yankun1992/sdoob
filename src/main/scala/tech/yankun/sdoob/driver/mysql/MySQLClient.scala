package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.{ByteBuf, Unpooled}
import tech.yankun.sdoob.driver.mysql.codec.{CommandCodec, InitialHandshakeCommandCodec}
import tech.yankun.sdoob.driver.mysql.command.InitialHandshakeCommand
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag.{CLIENT_CONNECT_ATTRS, CLIENT_CONNECT_WITH_DB, CLIENT_FOUND_ROWS, CLIENT_SUPPORTED_CAPABILITIES_FLAGS}
import tech.yankun.sdoob.driver.{Client, Command, CommandResponse, SqlConnectOptions}

import java.nio.channels.SocketChannel
import java.nio.charset.Charset
import java.util

class MySQLClient(options: MySQLConnectOptions) extends Client(options) {

  import MySQLClient._

  private var collation: MySQLCollation = _
  private var charsetEncoding: Charset = _
  private var useAffectedRows: Boolean = _
  //  private var sslMode: SslMode = _
  private var serverRsaPublicKey: ByteBuf = null

  private var initialCapabilitiesFlags: Int = initCapabilitiesFlags(options.getDatabase)

  //  private var pipeliningLimit: Int = options.getPipeliningLimit

  // mysql codec inflight
  private val inflight: util.ArrayDeque[CommandCodec[_, MySQLClient]] = new util.ArrayDeque[CommandCodec[_, MySQLClient]]()

  var clientCapabilitiesFlag: Int = _
  var encodingCharset: Charset = _

  var metadata: MySQLDatabaseMetadata = _

  private val socket = SocketChannel.open()
  init()

  def init(): Unit = {
    // connect to server
    socket.connect(server)
    // send login message
    sendStartupMessage(options.getUser, options.getPassword, options.getDatabase, collation,
      options.serverRsaPublicKeyValue, options.getProperties, options.getSslMode, initialCapabilitiesFlags,
      charsetEncoding, options.getAuthenticationPlugin
    )

    // handshake
    val buffer = Unpooled.directBuffer(4096)
    buffer.writeBytes(socket, 4096)

    decode(buffer)

    buffer.readBytes(socket, 4096)


  }

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
    inflight.addLast(codec)
    codec.encode(this)
  }

  protected def decode(in: ByteBuf): Unit = if (in.readableBytes() > 4) {
    val packetStart = in.readerIndex()
    val length = in.readUnsignedMediumLE()
    val sequenceId: Int = in.readUnsignedByte()

    if (in.readableBytes() >= length) {
      decodePacket(in.readSlice(length), length, sequenceId)
    } else {
      in.readerIndex(packetStart)
    }
  }

  private def decodePacket(payload: ByteBuf, length: Int, sequenceId: Int): Unit = {
    val codec = inflight.peek()
    codec.sequenceId += 1
    codec.decodePayload(payload, length)
    //TODO forget commands
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
  def wrap(cmd: Command): CommandCodec[_, MySQLClient] = {
    cmd match {
      case command: InitialHandshakeCommand =>
        new InitialHandshakeCommandCodec(command)
      case _ =>
        ???
    }
  }


}
