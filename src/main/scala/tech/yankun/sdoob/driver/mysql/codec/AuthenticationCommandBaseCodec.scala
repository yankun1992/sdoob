package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.log4s.getLogger
import tech.yankun.sdoob.driver.Client.ST_CLIENT_AUTHENTICATED
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.codec.AuthenticationCommandBaseCodec.{AUTH_PUBLIC_KEY_REQUEST_FLAG, FAST_AUTH_STATUS_FLAG, FULL_AUTHENTICATION_STATUS_FLAG}
import tech.yankun.sdoob.driver.mysql.command.AuthenticationCommandBase
import tech.yankun.sdoob.driver.mysql.utils.{BufferUtils, RsaPublicKeyEncryptor}

import java.nio.charset.StandardCharsets
import java.util

abstract class AuthenticationCommandBaseCodec[C <: AuthenticationCommandBase](cmd: C) extends CommandCodec[C, MySQLClient](cmd) {
  private[this] val logger = getLogger

  protected var authPluginData: Array[Byte] = _
  private var isWaitingForRsaPublicKey: Boolean = false

  protected def handleAuthMoreData(password: Array[Byte], payload: ByteBuf): Unit = {
    logger.info(s"client[${client.getClientId}] handle more auth data")
    payload.skipBytes(1)
    if (isWaitingForRsaPublicKey) {
      val serverRsaPublicKey = readRestOfPacketString(payload, StandardCharsets.UTF_8)
      client.release(payload)
      sendEncryptedPasswordWithServerRsaPublicKey(password, serverRsaPublicKey)
    } else {
      val flag = payload.readByte()
      client.release(payload)
      flag match {
        case FULL_AUTHENTICATION_STATUS_FLAG =>
          if (client.isSsl) {
            // TODO support ssl connect
          } else {
            val serverRsaPublicKey = cmd.serverRsaPublicKey
            if (serverRsaPublicKey == null) {
              isWaitingForRsaPublicKey = true
              val packet = client.getByteBuf()
              packet.writeMediumLE(1)
              packet.writeByte(sequenceId)
              packet.writeByte(AUTH_PUBLIC_KEY_REQUEST_FLAG)
              client.sendPacket(packet)
              client.release(packet)
            } else {
              sendEncryptedPasswordWithServerRsaPublicKey(password, serverRsaPublicKey.toString(StandardCharsets.UTF_8))
            }
          }
        case FAST_AUTH_STATUS_FLAG => // fast auth success
          logger.info(s"client[${client.getClientId}] fast auth success")
        case _ =>
      }

    }
  }

  protected def sendEncryptedPasswordWithServerRsaPublicKey(password: Array[Byte], serverRsaPublicKeyContent: String): Unit = {
    logger.info(s"client ${client.getClientId} send encrypted password with server rsa public key")
    val encryptedPassword: Array[Byte] = try {
      val input = util.Arrays.copyOf(password, password.length + 1)
      RsaPublicKeyEncryptor.encrypt(input, authPluginData, serverRsaPublicKeyContent)
    } catch {
      case e: Exception =>
        ???
    }

    sendBytesAsPacket(encryptedPassword)
  }

  protected def encodeConnectionAttributes(attributes: Map[String, String], packet: ByteBuf): Unit = {
    val buf = client.allocator.directBuffer(8 * 1024)
    for ((key, value) <- attributes) {
      BufferUtils.writeLengthEncodedString(buf, key, StandardCharsets.UTF_8)
      BufferUtils.writeLengthEncodedString(buf, value, StandardCharsets.UTF_8)
    }
    BufferUtils.writeLengthEncodedInteger(packet, buf.readableBytes())
    packet.writeBytes(buf)
    buf.release()
  }
}

object AuthenticationCommandBaseCodec {
  val NONCE_LENGTH = 20
  val AUTH_SWITCH_REQUEST_STATUS_FLAG = 0xFE

  val AUTH_MORE_DATA_STATUS_FLAG = 0x01
  protected val AUTH_PUBLIC_KEY_REQUEST_FLAG = 0x02
  protected val FAST_AUTH_STATUS_FLAG = 0x03
  protected val FULL_AUTHENTICATION_STATUS_FLAG = 0x04
}
