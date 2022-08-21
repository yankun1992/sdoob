package tech.yankun.sdoob.driver

import tech.yankun.sdoob.driver.common.CloseConnectionCommand

import java.net.SocketAddress
import java.nio.channels.SocketChannel

abstract class Client {
  val socket: SocketChannel

  def connect(remote: SocketAddress): Unit

  def close(): Unit = {
    this.write(CloseConnectionCommand)
    socket.close()
  }

  def write(command: Command): Unit

  def read(): CommandResponse
}
