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
import tech.yankun.sdoob.driver.DataFormat
import tech.yankun.sdoob.driver.command.QueryCommandBase
import tech.yankun.sdoob.driver.pg.datatype.DataType
import tech.yankun.sdoob.driver.pg.protocol.{ColumnDesc, Constants}
import tech.yankun.sdoob.driver.pg.utils.BufferUtils

class QueryCommandBaseCodec[C <: QueryCommandBase](cmd: C) extends PGCommandCodec[C](cmd) {
  protected val logger: Logger = getLogger

  protected var columns: Array[ColumnDesc] = _

  override protected def decodeMessage(payload: ByteBuf, messageType: Byte, messageLength: Int): Unit = {
    super.decodeMessage(payload, messageType, messageLength)
    messageType match {
      case Constants.MSG_TYPE_ROW_DESCRIPTION => decodeRowDesc(payload)
      case Constants.MSG_TYPE_DATA_ROW =>
    }
  }

  protected def decodeRowDesc(payload: ByteBuf): Unit = {
    columns = new Array[ColumnDesc](payload.readUnsignedShort())
    var index = 0
    while (index < columns.length) {
      val fieldName = BufferUtils.readCString(payload)
      val tableOid = payload.readInt()
      val columnAttrNum = payload.readShort()
      val typeOid = payload.readInt()
      val typeSize = payload.readShort()
      val typeModifier = payload.readInt()
      val encodeType = payload.readUnsignedShort()
      this.columns(index) = ColumnDesc(fieldName = fieldName, relationId = tableOid, dataType = DataType.valueOf(typeOid),
        dataFormat = DataFormat.valueOf(encodeType), relationAttributeNo = columnAttrNum, length = typeSize,
        typeModifier = typeModifier)
      index += 1
    }
    client.release(payload)
  }

  protected def decodeRow(payload: ByteBuf): Unit = {

  }

}
