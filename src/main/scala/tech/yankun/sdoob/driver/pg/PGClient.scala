package tech.yankun.sdoob.driver.pg

import tech.yankun.sdoob.driver.{Client, Command, CommandResponse}

import java.net.SocketAddress
import java.nio.channels.SocketChannel

class PGClient extends Client {
  val socket = SocketChannel.open()

  override def connect(remote: SocketAddress): Unit = ???


  override def write(command: Command): Unit = ???

  override def read(): CommandResponse = ???
}
