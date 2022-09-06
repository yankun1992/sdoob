package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import org.log4s.getLogger
import tech.yankun.sdoob.driver.command.QueryCommandBase
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.datatype.DataFormat
import tech.yankun.sdoob.driver.mysql.protocol.ColumnDefinition
import tech.yankun.sdoob.driver.mysql.protocol.Packets.{EOF_PACKET_HEADER, ERROR_PACKET_HEADER}
import tech.yankun.sdoob.driver.mysql.utils.BufferUtils

abstract class QueryCommandBaseCodec[C <: QueryCommandBase](cmd: C, val format: DataFormat)
  extends CommandCodec[C, MySQLClient](cmd) {

  import QueryCommandBaseCodec._

  private[this] val logger = getLogger

  var commandHandlerState: CommandHandlerState = INIT
  var columnDefinitions: Array[ColumnDefinition] = _
  var currentColumn: Int = 0

  override def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {
    commandHandlerState match {
      case INIT => handleInitPacket(payload)
      case HANDLING_COLUMN_DEFINITION => handleColumnDefinitions(payload)
      case COLUMN_DEFINITIONS_DECODING_COMPLETED =>
        skipEofPacketIfNeeded(payload)
        client.release(payload)
        handleColumnDefinitionsDecodingCompleted()
      case HANDLING_ROW_DATA_OR_END_PACKET =>
        handRows(payload, payloadLength)
    }
  }

  protected def handleInitPacket(payload: ByteBuf): Unit

  protected def handleResultsetColumnCountPacketBody(payload: ByteBuf): Unit = {
    val columnCount = BufferUtils.readLengthEncodedInteger(payload)
    client.release(payload)
    commandHandlerState = HANDLING_COLUMN_DEFINITION
    columnDefinitions = new Array[ColumnDefinition](columnCount.toInt)
  }

  protected def handleColumnDefinitions(payload: ByteBuf): Unit = {
    val definition = decodeColumnDefinition(payload)
    client.release(payload)
    columnDefinitions(currentColumn) = definition
    currentColumn += 1
    if (currentColumn == columnDefinitions.length) {
      // all column definition have been decoded, switch to column definitions decoding completed state
      if (isDeprecatingEofFlagEnabled) {
        // we enabled the DEPRECATED_EOF flag and don't need to accept an EOF_Packet
        handleColumnDefinitionsDecodingCompleted()
      } else {
        // we need to decode an EOF_Packet before handling rows, to be compatible with MySQL version below 5.7.5
        commandHandlerState = COLUMN_DEFINITIONS_DECODING_COMPLETED
      }
    }
  }

  protected def handleColumnDefinitionsDecodingCompleted(): Unit = {
    commandHandlerState = HANDLING_ROW_DATA_OR_END_PACKET

  }

  protected def handRows(payload: ByteBuf, length: Int): Unit = {
    /*
      Result set row can begin with 0xfe byte (when using text protocol with a field length > 0xffffff)
      To ensure that packets beginning with 0xfe correspond to the ending packet (EOF_Packet or OK_Packet with a 0xFE header),
      the packet length must be checked and must be less than 0xffffff in length.
   */
    val first = payload.getUnsignedByte(payload.readerIndex())
    if (first == ERROR_PACKET_HEADER) {
      handErrorPacket(payload)
      client.release(payload)
    } else if (first == EOF_PACKET_HEADER && length < 0xFFFFFF) {
      // enabling CLIENT_DEPRECATE_EOF capability will receive an OK_Packet with a EOF_Packet header here
      // we need check this is not a row data by checking packet length < 0xFFFFFF
      var serverStatusFlags: Int = 0
      var affectedRows: Long = -1
      var lastInsertId: Long = -1
      if (isDeprecatingEofFlagEnabled) {
        val ok = decodeOkPacket(payload)
        serverStatusFlags = ok.serverStatusFlags
        affectedRows = ok.affectedRows
        lastInsertId = ok.lastInsertId
      } else {
        val eof = decodeEofPacket(payload)
        serverStatusFlags = eof.serverStatusFlags
      }
      client.release(payload)
      handleSingleDecodingCompleted(serverStatusFlags, affectedRows, lastInsertId)
    } else {
      logger.info("decode rows")
    }

  }

  protected def handleSingleDecodingCompleted(serverFlags: Int, affected: Long, lastId: Long): Unit = {

  }

}

object QueryCommandBaseCodec {
  trait CommandHandlerState

  object INIT extends CommandHandlerState

  object HANDLING_COLUMN_DEFINITION extends CommandHandlerState

  object COLUMN_DEFINITIONS_DECODING_COMPLETED extends CommandHandlerState

  object HANDLING_ROW_DATA_OR_END_PACKET extends CommandHandlerState
}
