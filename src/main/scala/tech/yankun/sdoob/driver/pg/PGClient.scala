package tech.yankun.sdoob.driver.pg

import tech.yankun.sdoob.driver.{Client, Command, CommandResponse, SqlConnectOptions}

import java.net.SocketAddress
import java.nio.channels.SocketChannel

class PGClient(options: SqlConnectOptions) extends Client(options) {
  val socket = SocketChannel.open()

  override def initializeConfiguration(options: SqlConnectOptions): Unit = ???

  override def connect(remote: SocketAddress): Unit = ???


  override def write(command: Command): Unit = ???

  override def read(): CommandResponse = ???

  override def close(): Unit = ???
}
