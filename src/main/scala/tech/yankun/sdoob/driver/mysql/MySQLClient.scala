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
import tech.yankun.sdoob.driver.command._
import tech.yankun.sdoob.driver.mysql.codec._
import tech.yankun.sdoob.driver.mysql.command.InitialHandshakeCommand
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag._
import tech.yankun.sdoob.driver.{Client, SqlConnectOptions}

import java.nio.charset.Charset

class MySQLClient(options: MySQLConnectOptions, parent: Option[MySQLPool] = None)
  extends Client[MySQLPool, MySQLCommandCodec[_ <: Command]](options, parent) {

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

  override def init(): Unit = {
    sendStartupMessage(options.getUser, options.getPassword, options.getDatabase, collation,
      options.serverRsaPublicKeyValue, options.getProperties, options.getSslMode, initialCapabilitiesFlags,
      charsetEncoding, options.getAuthenticationPlugin
    )
    super.init()
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
    val codec: MySQLCommandCodec[_ <: Command] = wrap(command)
    if (inflight.isEmpty) flightCodec = Some(codec)
    inflight.addLast(codec)
    codec.encode(this)
  }

  override def decodePacket(packet: ByteBuf): Unit = {
    val packetStart = packet.readerIndex()
    val length = packet.readUnsignedMediumLE()
    val sequenceId: Int = packet.readUnsignedByte()
    val codec = inflight.peek()
    flightCodec = Some(codec)
    codec.sequenceId = sequenceId + 1
    codec.decodePayload(packet, length)
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

  override def wrap(cmd: Command): MySQLCommandCodec[_ <: Command] = cmd match {
    case command: InitialHandshakeCommand =>
      new InitialHandshakeMySQLCommandCodec(command)
    case command: SimpleQueryCommand =>
      new SimpleQueryMySQLCommandCodec(command)
    case command: SdoobSimpleQueryCommand => new SdoobSimpleQueryMySQLCommandCodec(command)
    case CloseConnectionCommand => new CloseConnectionMySQLCommandCodec(CloseConnectionCommand)
    case _ =>
      throw new IllegalStateException(s"not supported database command: ${cmd}")
  }

}

object MySQLClient {


}
