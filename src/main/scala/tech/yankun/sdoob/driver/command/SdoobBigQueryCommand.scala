package tech.yankun.sdoob.driver.command

import io.netty.buffer.ByteBuf

abstract class SdoobBigQueryCommand(val packet: ByteBuf) extends Command
