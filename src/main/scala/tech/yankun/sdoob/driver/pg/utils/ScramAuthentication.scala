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

package tech.yankun.sdoob.driver.pg.utils

import com.ongres.scram.client.{ScramClient, ScramSession}
import com.ongres.scram.common.exception.ScramException
import com.ongres.scram.common.stringprep.StringPreparations
import io.netty.buffer.ByteBuf

import java.nio.charset.StandardCharsets

case class ScramAuthentication(username: String, password: String) {

  import ScramAuthentication._

  private var session: ScramSession = _
  private var clientFinalProcessor: ScramSession#ClientFinalProcessor = _

  /**
   * The client selects one of the supported mechanisms from the list,
   * and sends a SASLInitialResponse message to the server.
   * The message includes the name of the selected mechanism, and
   * an optional Initial Client Response, if the selected mechanism uses that.
   *
   * @param buffer
   * @return
   */
  def initialSaslMsg(buffer: ByteBuf): ScramClientInitialMessage = {
    val mechanisms = collection.mutable.ArrayBuffer.empty[String]
    while (0 != buffer.getByte(buffer.readerIndex())) mechanisms.append(BufferUtils.readCString(buffer))
    buffer.skipBytes(1)
    if (mechanisms.isEmpty)
      throw new UnsupportedOperationException("SASL Authentication : the server returned no mechanism")
    if (!mechanisms.contains(SCRAM_SHA_256))
      throw new UnsupportedOperationException(
        s"SASL Authentication : only SCRAM-SHA-256 is currently supported, server wants ${mechanisms.mkString("[", ", ", "]")}")
    val scramClient = ScramClient.channelBinding(ScramClient.ChannelBinding.NO)
      .stringPreparation(StringPreparations.NO_PREPARATION)
      .selectMechanismBasedOnServerAdvertised(mechanisms.toSeq: _*)
      .setup()

    session = scramClient.scramSession(username)

    ScramClientInitialMessage(scramClient.getScramMechanism.getName, session.clientFirstMessage())
  }

  /**
   * One or more server-challenge and client-response message will follow.
   * Each server-challenge is sent in an AuthenticationSASLContinue message,
   * followed by a response from client in an SASLResponse message.
   * The particulars of the messages are mechanism specific.
   */
  def recvServerFirstMsg(buffer: ByteBuf): String = {
    val msg = buffer.readCharSequence(buffer.readableBytes(), StandardCharsets.UTF_8).toString
    val processor = try {
      session.receiveServerFirstMessage(msg)
    } catch {
      case e: ScramException =>
        throw new UnsupportedOperationException(e)
    }
    clientFinalProcessor = processor.clientFinalProcessor(password)

    clientFinalProcessor.clientFinalMessage()
  }

  /**
   * Finally, when the authentication exchange is completed successfully,
   * the server sends an AuthenticationSASLFinal message, followed immediately by an AuthenticationOk message.
   * The AuthenticationSASLFinal contains additional server-to-client data,
   * whose content is particular to the selected authentication mechanism.
   * If the authentication mechanism doesn't use additional data that's sent at completion,
   * the AuthenticationSASLFinal message is not sent
   */
  def checkServerFinalMsg(buffer: ByteBuf, length: Int): Unit = {
    val serverFinalMsg = buffer.readCharSequence(length, StandardCharsets.UTF_8).toString
    try {
      clientFinalProcessor.receiveServerFinalMessage(serverFinalMsg)
    } catch {
      case e: Exception =>
        throw new UnsupportedOperationException(e)
    }
  }
}


object ScramAuthentication {
  case class ScramClientInitialMessage(mechanism: String, message: String)

  private val SCRAM_SHA_256 = "SCRAM-SHA-256"
}