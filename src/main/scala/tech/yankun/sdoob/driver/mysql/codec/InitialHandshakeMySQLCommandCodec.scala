package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.log4s._
import tech.yankun.sdoob.driver.Client.ST_CLIENT_AUTHENTICATED
import tech.yankun.sdoob.driver.mysql.codec.AuthenticationMySQLCommandBaseCodec.{AUTH_MORE_DATA_STATUS_FLAG, AUTH_SWITCH_REQUEST_STATUS_FLAG, NONCE_LENGTH}
import tech.yankun.sdoob.driver.mysql.command.InitialHandshakeCommand
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag
import tech.yankun.sdoob.driver.mysql.protocol.CapabilitiesFlag._
import tech.yankun.sdoob.driver.mysql.protocol.Packets.{ERROR_PACKET_HEADER, OK_PACKET_HEADER, PACKET_PAYLOAD_LENGTH_LIMIT}
import tech.yankun.sdoob.driver.mysql.utils.{BufferUtils, CachingSha2Authenticator, Native41Authenticator}
import tech.yankun.sdoob.driver.mysql.{MySQLAuthenticationPlugin, MySQLDatabaseMetadata, SslMode}

import java.nio.charset.StandardCharsets

class InitialHandshakeMySQLCommandCodec(cmd: InitialHandshakeCommand)
  extends AuthenticationMySQLCommandBaseCodec[InitialHandshakeCommand](cmd) {

  import InitialHandshakeMySQLCommandCodec._

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
    logger.info(s"client[${client.getClientId}] handle initial handshake")
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
    val serverFlags = lowerFlags | (upperFlags << 16)

    // length of the combined auth_plugin_data (scramble)
    val isClientPluginAuthSupported = (serverFlags & CapabilitiesFlag.CLIENT_PLUGIN_AUTH) != 0
    val lenOfAuthPluginData: Short = if (isClientPluginAuthSupported) {
      payload.readUnsignedByte()
    } else {
      payload.skipBytes(1)
      0
    }

    // 10 bytes reserved
    payload.skipBytes(10)

    // Reset of the plugin provided dara
    payload.readBytes(authPluginData, AUTH_PLUGIN_DATA_PART1_LENGTH,
      math.max(NONCE_LENGTH - AUTH_PLUGIN_DATA_PART1_LENGTH, lenOfAuthPluginData - 9))
    payload.readByte() // reserved byte

    // Assume the server supports auth plugin
    val serverPluginName = BufferUtils.readNullTerminatedString(payload, StandardCharsets.UTF_8)
    client.release(payload)

    val upgradeSsl = cmd.sslMode match {
      case SslMode("disabled") => false
      case SslMode("preferred") => false
      case SslMode("required") => false
      case SslMode("verify_ca") => false
      case SslMode("verify_identity") => true
      case _ => false
    }
    if (upgradeSsl) {
      logger.warn("ssl connect is not support")
      sendHandshakeResMsg(serverPluginName, cmd.authenticationPlugin, authPluginData, serverFlags)
    } else {
      sendHandshakeResMsg(serverPluginName, cmd.authenticationPlugin, authPluginData, serverFlags)
    }

  }

  private def sendHandshakeResMsg(serverPluginName: String, authPlugin: MySQLAuthenticationPlugin, nonce: Array[Byte],
                                  serverFlags: Int): Unit = {
    logger.info(s"client[${client.getClientId}] send handshake response message")
    client.clientCapabilitiesFlag &= serverFlags
    val clientPluginName = if (authPlugin == MySQLAuthenticationPlugin.DEFAULT) serverPluginName else authPlugin.value
    val packet = client.getByteBuf()
    val packetStartIdx = packet.writerIndex
    packet.writeMediumLE(0)
    packet.writeByte(sequenceId)
    packet.writeIntLE(client.clientCapabilitiesFlag)
    packet.writeIntLE(PACKET_PAYLOAD_LENGTH_LIMIT)
    packet.writeByte(cmd.collation.collationId)
    packet.writeZero(23)
    BufferUtils.writeNullTerminatedString(packet, cmd.username, StandardCharsets.UTF_8)
    var authMethod = clientPluginName
    if (cmd.password == null || cmd.password.isEmpty) {
      packet.writeByte(0)
    } else {
      val authResponse = authMethod match {
        case "mysql_native_password" =>
          Native41Authenticator.encode(cmd.password.getBytes(StandardCharsets.UTF_8), nonce)
        case "caching_sha2_password" =>
          CachingSha2Authenticator.encode(cmd.password.getBytes(StandardCharsets.UTF_8), nonce)
        case "mysql_clear_password" =>
          cmd.password.getBytes(StandardCharsets.UTF_8)
        case _ =>
          logger.warn(s"Unknown authentication method: ${authMethod},  the client will try to use mysql_native_password instead.")
          authMethod = "mysql_native_password"
          Native41Authenticator.encode(cmd.password.getBytes(StandardCharsets.UTF_8), nonce)
      }
      if ((client.clientCapabilitiesFlag & CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
        BufferUtils.writeLengthEncodedInteger(packet, authResponse.length)
        packet.writeBytes(authResponse)
      } else if ((client.clientCapabilitiesFlag & CLIENT_SECURE_CONNECTION) != 0) {
        packet.writeByte(authResponse.length)
        packet.writeBytes(authResponse)
      } else {
        packet.writeByte(0)
      }
    }
    if ((client.clientCapabilitiesFlag & CLIENT_CONNECT_WITH_DB) != 0) {
      BufferUtils.writeNullTerminatedString(packet, cmd.database, StandardCharsets.UTF_8)
    }
    if ((client.clientCapabilitiesFlag & CLIENT_PLUGIN_AUTH) != 0) {
      BufferUtils.writeNullTerminatedString(packet, authMethod, StandardCharsets.UTF_8)
    }
    if ((client.clientCapabilitiesFlag & CLIENT_CONNECT_ATTRS) != 0) {
      encodeConnectionAttributes(cmd.connectionAttributes, packet)
    }

    // set payload length
    val payloadLen = packet.writerIndex() - packetStartIdx - 4
    packet.setMediumLE(packetStartIdx, payloadLen)

    // send
    client.sendPacket(packet)
    client.release(packet)
  }


  private def handleAuthentication(payload: ByteBuf): Unit = {
    logger.info(s"client[${client.getClientId}] handle authentication")
    val header = payload.getUnsignedByte(payload.readerIndex())
    header match {
      case OK_PACKET_HEADER =>
        status = ST_CONNECTED
        client.release(payload)
        client.setStatus(ST_CLIENT_AUTHENTICATED)
        client.handleCommandResponse("success")
      case ERROR_PACKET_HEADER =>
        client.handleCommandResponse(new RuntimeException(s"client[${client.getClientId}] auth failed"))
      case AUTH_SWITCH_REQUEST_STATUS_FLAG =>
        handleAuthSwitchRequest(cmd.password.getBytes(StandardCharsets.UTF_8), payload)
      case AUTH_MORE_DATA_STATUS_FLAG =>
        handleAuthMoreData(cmd.password.getBytes(StandardCharsets.UTF_8), payload)
      case _ =>
    }
  }

  private def handleAuthSwitchRequest(password: Array[Byte], payload: ByteBuf): Unit = {
    // Protocol::AuthSwitchRequest
    payload.skipBytes(1)
    val pluginName = BufferUtils.readNullTerminatedString(payload, StandardCharsets.UTF_8)
    val nonce = new Array[Byte](NONCE_LENGTH)
    payload.readBytes(nonce)
    client.release(payload)
    val authRes = pluginName match {
      case "mysql_native_password" => Native41Authenticator.encode(password, nonce)
      case "caching_sha2_password" => CachingSha2Authenticator.encode(password, nonce)
      case "mysql_clear_password" => password
      case _ =>
        client.handleCommandResponse(new UnsupportedOperationException(s"Unsupported authentication method: ${pluginName}"))
        ???
    }
    sendBytesAsPacket(authRes)
  }

  def isTlsSupportedByServer(serverCapabilitiesFlags: Int): Boolean =
    (serverCapabilitiesFlags & CLIENT_SSL) != 0


}

object InitialHandshakeMySQLCommandCodec {
  private val AUTH_PLUGIN_DATA_PART1_LENGTH = 8

  private val ST_CONNECTING = 0
  private val ST_AUTHENTICATING = 1
  private val ST_CONNECTED = 2


}