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

package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.checker.PacketChecker._
import tech.yankun.sdoob.driver.command._
import tech.yankun.sdoob.driver.mysql.codec._
import tech.yankun.sdoob.driver.mysql.command.InitialHandshakeCommand
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag._
import tech.yankun.sdoob.driver.{Client, SqlConnectOptions}

import java.net.SocketException
import java.nio.charset.Charset

class MySQLClient(options: MySQLConnectOptions, parent: Option[MySQLPool] = None)
  extends Client[MySQLPool, MySQLCommandCodec[_]](options, parent) {

  import MySQLClient._

  private var collation: MySQLCollation = _
  private var charsetEncoding: Charset = _
  private var useAffectedRows: Boolean = _
  //  private var sslMode: SslMode = _
  private var serverRsaPublicKey: ByteBuf = _

  private var initialCapabilitiesFlags: Int = initCapabilitiesFlags(options.getDatabase)

  //  private var pipeliningLimit: Int = options.getPipeliningLimit

  var clientCapabilitiesFlag: Int = _
  var encodingCharset: Charset = _

  var metadata: MySQLDatabaseMetadata = _

  val packetChecker = new MySQLPacketChecker

  def currentCodec: MySQLCommandCodec[_] = flightCodec

  def codecCompleted: Boolean = inflight.isEmpty || isClosed

  def init(): Unit = {
    sendStartupMessage(options.getUser, options.getPassword, options.getDatabase, collation,
      options.serverRsaPublicKeyValue, options.getProperties, options.getSslMode, initialCapabilitiesFlags,
      charsetEncoding, options.getAuthenticationPlugin
    )
    inited = true
    logger.info(s"client[${clientId}] send startup message")
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
    val codec: MySQLCommandCodec[_] = wrap(command)
    if (inflight.isEmpty) flightCodec = codec
    inflight.addLast(codec)
    codec.encode(this)
  }


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

  def getByteBuf(inRead: Boolean = false): ByteBuf = {
    if (bufferRemain && inRead) {
      buffer
    } else if (inRead) {
      this.buffer = allocator.directBuffer(BUFFER_SIZE)
      this.buffer
    } else {
      allocator.directBuffer(BUFFER_SIZE)
    }
  }

  def release(buffer: ByteBuf): Unit = {
    if (buffer != this.buffer) {
      buffer.release()
    } else if (bufferRemain) {} else if (!bufferRemain && parent.isEmpty) {
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

  private def decodePacket(payload: ByteBuf): Unit = {
    val packetStart = payload.readerIndex()
    val length = payload.readUnsignedMediumLE()
    val sequenceId: Int = payload.readUnsignedByte()
    val codec = inflight.peek()
    flightCodec = codec
    codec.sequenceId = sequenceId + 1
    codec.decodePayload(payload, length)
    if (bufferRemain && buffer.isReadable) buffer.readerIndex(packetStart + 4 + length)
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

  override def wrap(cmd: Command): MySQLCommandCodec[_] = cmd match {
    case command: InitialHandshakeCommand =>
      new InitialHandshakeMySQLCommandCodec(command)
    case command: SimpleQueryCommand =>
      new SimpleQueryMySQLCommandCodec(command)
    case command: SdoobSimpleQueryCommand => new SdoobSimpleQueryMySQLCommandCodec(command)
    case CloseConnectionCommand => new CloseConnectionMySQLCommandCodec(CloseConnectionCommand)
    case _ =>
      ???
  }


}

object MySQLClient {
  protected val BUFFER_SIZE: Int = 8 * 1024
  protected val BIG_BUFFER_SIZE: Int = 128 * 1024


}
