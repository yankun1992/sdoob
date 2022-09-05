package tech.yankun.sdoob.driver.mysql.codec

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.QueryCommandBase
import tech.yankun.sdoob.driver.mysql.MySQLClient
import tech.yankun.sdoob.driver.mysql.datatype.DataFormat

abstract class QueryCommandBaseCodec[C <: QueryCommandBase](cmd: C, val format: DataFormat) extends CommandCodec[C, MySQLClient](cmd) {

  import QueryCommandBaseCodec._

  var commandHandlerState: CommandHandlerState = INIT

  override def decodePayload(payload: ByteBuf, payloadLength: Int): Unit = {
    commandHandlerState match {
      case INIT =>
        handleInitPacket(payload)
      case HANDLING_COLUMN_DEFINITION => ???
      case COLUMN_DEFINITIONS_DECODING_COMPLETED => ???
      case HANDLING_ROW_DATA_OR_END_PACKET => ???
    }
  }

  protected def handleInitPacket(payload: ByteBuf): Unit
}

object QueryCommandBaseCodec {
  trait CommandHandlerState

  object INIT extends CommandHandlerState

  object HANDLING_COLUMN_DEFINITION extends CommandHandlerState

  object COLUMN_DEFINITIONS_DECODING_COMPLETED extends CommandHandlerState

  object HANDLING_ROW_DATA_OR_END_PACKET extends CommandHandlerState
}
