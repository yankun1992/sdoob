package tech.yankun.sdoob.driver.command

import io.netty.buffer.ByteBuf

case class SdoobBigInsertCommand(override val packet: ByteBuf) extends SdoobBigQueryCommand(packet)
