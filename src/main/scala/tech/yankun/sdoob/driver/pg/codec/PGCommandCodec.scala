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
import tech.yankun.sdoob.driver.codec.CommandCodec
import tech.yankun.sdoob.driver.command.Command
import tech.yankun.sdoob.driver.pg.PGClient
import tech.yankun.sdoob.driver.pg.protocol.Constants
import tech.yankun.sdoob.driver.pg.response.ErrorResponse
import tech.yankun.sdoob.driver.pg.utils.BufferUtils

/**
 * abstract class for postgresql packet message codec
 *
 * @param cmd database command
 * @tparam C type of command
 */
abstract class PGCommandCodec[C <: Command](cmd: C) extends CommandCodec[C, PGClient](cmd) {

  /**
   * decode message packet received from database
   *
   * @param payload       message packet payload
   * @param payloadLength payload length
   */
  override final def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {
    val messageType = payload.readByte()
    val messageLength = payload.readInt() - 4
    decodeMessage(payload, messageType, messageLength)
  }

  protected def decodeMessage(payload: ByteBuf, messageType: Byte, messageLength: Int): Unit = {
    messageType match {
      case Constants.MSG_TYPE_ERROR_RESPONSE => decodeError(payload)
      case _ =>
    }
  }

  private def decodeError(packet: ByteBuf): Unit = {
    val response = new ErrorResponse
    decodeErrorOrNotice(response, packet)
    client.handleCommandResponse(response.toException)
  }

  private def decodeErrorOrNotice(response: ErrorResponse, buffer: ByteBuf): Unit = {
    var tp = buffer.readByte()
    while (tp != 0) {
      tp match {
        case Constants.ERR_OR_NOTICE_SEVERITY => response.setSeverity(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_CODE => response.setCode(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_MESSAGE => response.setMessage(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_DETAIL => response.setDetail(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_HINT => response.setHint(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_INTERNAL_POSITION => response.setInternalPosition(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_INTERNAL_QUERY => response.setInternalQuery(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_POSITION => response.setPosition(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_WHERE => response.setWhere(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_FILE => response.setFile(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_LINE => response.setLine(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_ROUTINE => response.setRoutine(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_SCHEMA => response.setSchema(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_TABLE => response.setTable(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_COLUMN => response.setColumn(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_DATA_TYPE => response.setDataType(BufferUtils.readCString(buffer))
        case Constants.ERR_OR_NOTICE_CONSTRAINT => response.setConstraint(BufferUtils.readCString(buffer))
        case _ => BufferUtils.readCString(buffer)
      }
      tp = buffer.readByte()
    }
  }

  protected def decodeReadyForQuery(payload: ByteBuf): Unit = {
    val id: Byte = payload.readByte()
    if (id == 'I') {} else if (id == 'T') {}
  }

}

object PGCommandCodec {
  val PASSWORD_MESSAGE: Byte = 'p'
  val QUERY: Byte = 'Q'
  val TERMINATE: Byte = 'X'
  val PARSE: Byte = 'P'
  val BIND: Byte = 'B'
  val DESCRIBE: Byte = 'D'
  val EXECUTE: Byte = 'E'
  val CLOSE: Byte = 'C'
  val SYNC: Byte = 'S'
}
