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

package tech.yankun.sdoob.driver.pg.codec

import io.netty.buffer.ByteBuf
import org.log4s.{Logger, getLogger}
import tech.yankun.sdoob.driver.Client
import tech.yankun.sdoob.driver.command.InitCommand
import tech.yankun.sdoob.driver.pg.PGClient
import tech.yankun.sdoob.driver.pg.protocol.Constants
import tech.yankun.sdoob.driver.pg.utils.{BufferUtils, MD5Authentication, ScramAuthentication}

import java.nio.charset.StandardCharsets

class InitCommandCodec(cmd: InitCommand) extends PGCommandCodec[InitCommand](cmd) {

  private val logger: Logger = getLogger

  private var encoding: String = _

  private var scram: ScramAuthentication = _

  override def encode(client: PGClient): Unit = {
    super.encode(client)
    val packet = client.getByteBuf()
    val start = packet.writerIndex()
    // payload length, will reset latter
    packet.writeInt(0)
    // protocol version
    packet.writeShort(3)
    packet.writeShort(0)

    BufferUtils.writeCString(packet, "user".getBytes(StandardCharsets.UTF_8))
    BufferUtils.writeCString(packet, cmd.username)
    BufferUtils.writeCString(packet, "database".getBytes(StandardCharsets.UTF_8))
    BufferUtils.writeCString(packet, cmd.database)

    for ((key, value) <- cmd.properties) {
      BufferUtils.writeCString(packet, key)
      BufferUtils.writeCString(packet, value)
    }

    packet.writeByte(0)
    // reset payload length
    packet.setInt(start, packet.writerIndex() - start)

    client.sendPacket(packet)
    client.release(packet)
  }

  override protected def decodeMessage(payload: ByteBuf, messageType: Byte, messageLength: Int): Unit = {
    super.decodeMessage(payload, messageType, messageLength)
    messageType match {
      case Constants.MSG_TYPE_READY_FOR_QUERY => decodeReadyForQuery(payload)
      case Constants.MSG_TYPE_AUTHENTICATION => decodeAuthentication(payload, messageLength)
      case Constants.MSG_TYPE_PARAMETER_STATUS => decodeParameterStatus(payload)
      case Constants.MSG_TYPE_BACKEND_KEY_DATA => decodeBackendKeyData(payload)
    }
  }

  private def decodeAuthentication(payload: ByteBuf, messageLength: Int): Unit = {
    val tp = payload.readInt()
    tp match {
      case Constants.AUTH_TYPE_OK =>
        client.release(payload)
        handleAuthenticationOK()
      case Constants.AUTH_TYPE_MD5_PASSWORD =>
        val salt = new Array[Byte](4)
        payload.readBytes(salt)
        client.release(payload)
        handleAuthenticationMD5Password(salt)
      case Constants.AUTH_TYPE_CLEARTEXT_PASSWORD =>
        client.release(payload)
        handleAuthenticationClearTextPassword()
      case Constants.AUTH_TYPE_SASL =>
        handleAuthenticationSasl(payload)
      case Constants.AUTH_TYPE_SASL_CONTINUE =>
        handleAuthenticationSaslContinue(payload)
      case Constants.AUTH_TYPE_SASL_FINAL =>
        handleAuthenticationSaslFinal(payload, messageLength - 4)
      case _ =>
        throw new UnsupportedOperationException(s"Authentication type ${tp} is not supported in the client")
    }
  }

  private def handleAuthenticationOK(): Unit = {
    //    client.handleCommandResponse(s"client[${client.getClientId}] authentication ok")
    //    client.setStatus(Client.ST_CLIENT_AUTHENTICATED)
  }

  private def handleAuthenticationSasl(buffer: ByteBuf): Unit = {
    scram = new ScramAuthentication(cmd.username, cmd.password)
    val initialMsg = scram.initialSaslMsg(buffer)
    client.release(buffer)
    val packet = client.getByteBuf()
    packet.writeByte(PGCommandCodec.PASSWORD_MESSAGE)
    val pos = packet.writerIndex()
    packet.writeInt(0)

    BufferUtils.writeCString(packet, initialMsg.mechanism)
    val msgPos = packet.writerIndex()
    packet.writeInt(0)
    packet.writeCharSequence(initialMsg.message, StandardCharsets.UTF_8)

    packet.setInt(msgPos, packet.writerIndex() - msgPos - 4)
    packet.setInt(pos, packet.writerIndex() - pos)

    // send packet
    client.sendPacket(packet)
    client.release(packet)
    logger.debug("handled SASL")
  }

  private def handleAuthenticationSaslContinue(buffer: ByteBuf): Unit = {
    val msg = scram.recvServerFirstMsg(buffer)
    client.release(buffer)
    val packet = client.getByteBuf()
    packet.writeByte(PGCommandCodec.PASSWORD_MESSAGE)
    val lenPos = packet.writerIndex()
    packet.writeInt(0)
    packet.writeCharSequence(msg, StandardCharsets.UTF_8)
    packet.setInt(lenPos, packet.writerIndex() - lenPos)

    client.sendPacket(packet)
    client.release(packet)
    logger.debug("handled SASL continue")
  }

  private def handleAuthenticationSaslFinal(buffer: ByteBuf, messageLength: Int): Unit = {
    scram.checkServerFinalMsg(buffer, messageLength)
    client.release(buffer)
    logger.debug("handled SASL final")
  }

  private def handleAuthenticationMD5Password(salt: Array[Byte]): Unit = {
    val hash = MD5Authentication.encode(cmd.username, cmd.password, salt)
    sendPasswordMsg(hash)
  }

  private def handleAuthenticationClearTextPassword(): Unit = {
    sendPasswordMsg(cmd.password)
  }

  private def decodeParameterStatus(buffer: ByteBuf): Unit = {
    logger.debug("decodeParameterStatus")
    val key = BufferUtils.readCString(buffer)
    val value = BufferUtils.readCString(buffer)
    client.release(buffer)
    if (key == "client_encoding") encoding = value
    if (key == "server_version") logger.warn(s"server_version is ${value}, set it to PGClient metadata")
  }

  private def decodeBackendKeyData(buffer: ByteBuf): Unit = {
    val processId = buffer.readInt()
    val secretKey = buffer.readInt()
    client.release(buffer)
    logger.warn(s"processId: ${processId}, secretKey: ${secretKey}, set it to PGClient")
  }

  override protected def decodeReadyForQuery(payload: ByteBuf): Unit = {
    super.decodeReadyForQuery(payload)
    client.release(payload)
    client.handleCommandResponse("auth success")
    client.setStatus(Client.ST_CLIENT_AUTHENTICATED)
  }

  private def sendPasswordMsg(msg: String): Unit = {
    val packet = client.getByteBuf()
    packet.writeByte(PGCommandCodec.PASSWORD_MESSAGE)
    val lenPos = packet.writerIndex()
    packet.writeByte(0)
    BufferUtils.writeCString(packet, msg)
    packet.setInt(lenPos, packet.writerIndex() - lenPos)

    client.sendPacket(packet)
    client.release(packet)
  }
}
