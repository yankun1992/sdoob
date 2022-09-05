package tech.yankun.sdoob.driver.pg

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.command.{Command, CommandResponse}
import tech.yankun.sdoob.driver.{Client, SqlConnectOptions}

import java.nio.channels.SocketChannel

class PGClient(options: SqlConnectOptions) extends Client(options) {
  val socket = SocketChannel.open()

  override def initializeConfiguration(options: SqlConnectOptions): Unit = ???

  override def write(command: Command): Unit = ???

  override def sendPacket(packet: ByteBuf): Unit = ???

  override def read(): CommandResponse = ???

  override def close(): Unit = ???
}
