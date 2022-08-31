package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.log4s._
import tech.yankun.sdoob.driver.mysql.MySQLDatabaseMetadata
import tech.yankun.sdoob.driver.mysql.codec.AuthenticationCommandBaseCodec.NONCE_LENGTH
import tech.yankun.sdoob.driver.mysql.command.InitialHandshakeCommand
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag.{CLIENT_DEPRECATE_EOF, CLIENT_SSL}
import tech.yankun.sdoob.driver.mysql.utils.BufferUtils

import java.nio.charset.StandardCharsets

class InitialHandshakeCommandCodec(cmd: InitialHandshakeCommand)
  extends AuthenticationCommandBaseCodec[InitialHandshakeCommand](cmd) {

  import InitialHandshakeCommandCodec._

  private[this] val logger = getLogger

  private var status = ST_CONNECTING

  override def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {
    status match {
      case ST_CONNECTING =>
        handleInitialHandshake(payload)
        status = ST_AUTHENTICATING
      case ST_AUTHENTICATING =>
        handleAuthentication(payload)
    }
  }

  private def handleInitialHandshake(payload: ByteBuf): Unit = {
    client.clientCapabilitiesFlag = cmd.initialCapabilitiesFlags
    client.encodingCharset = cmd.charsetEncoding
    val protocolVersion: Short = payload.readUnsignedByte()

    val serverVersion = BufferUtils.readNullTerminatedString(payload, StandardCharsets.US_ASCII)
    val md = MySQLDatabaseMetadata.parse(serverVersion)
    client.metadata = md

    if (md.majorVersion == 5 && (md.minorVersion < 7 || (md.minorVersion == 7 && md.microVersion < 5))) {
      // EOF_HEADER has to be enabled for older MySQL version which does not support the CLIENT_DEPRECATE_EOF flag
    } else {
      client.clientCapabilitiesFlag |= CLIENT_DEPRECATE_EOF
    }

    val connectionId = payload.readUnsignedIntLE()

    // read first of scramble
    authPluginData = new Array[Byte](NONCE_LENGTH)
    payload.readBytes(authPluginData, 0, AUTH_PLUGIN_DATA_PART1_LENGTH)

    // filter
    payload.readByte()

    // read lower 2 bytes of Capabilities flags
    val lowerFlags = payload.readUnsignedShortLE()

    val charset: Short = payload.readUnsignedByte()

    val statusFlags: Int = payload.readUnsignedShortLE()

    // read upper 2 bytes of Capabilities flags
    val upperFlags = payload.readUnsignedShortLE()

  }


  private def handleAuthentication(payload: ByteBuf): Unit = {}


  def isTlsSupportedByServer(serverCapabilitiesFlags: Int): Boolean =
    (serverCapabilitiesFlags & CLIENT_SSL) != 0


}

object InitialHandshakeCommandCodec {
  private val AUTH_PLUGIN_DATA_PART1_LENGTH = 8

  private val ST_CONNECTING = 0
  private val ST_AUTHENTICATING = 1
  private val ST_CONNECTED = 2


}