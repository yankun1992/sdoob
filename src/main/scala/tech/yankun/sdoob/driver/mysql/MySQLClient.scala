package tech.yankun.sdoob.driver.mysql

import tech.yankun.sdoob.driver.common._
import tech.yankun.sdoob.driver.{Client, Command, CommandResponse}

import java.net.SocketAddress
import java.nio.channels.SocketChannel

class MySQLClient extends Client {
  val socket = SocketChannel.open()

  override def connect(remote: SocketAddress): Unit = {
    socket.connect(remote)

  }

  override def write(command: Command): Unit = {
    command match {
      case InitCommand(conn, username, password, database, properties) =>
        ???
      case CloseConnectionCommand =>
        ???
      case SimpleQueryCommand() =>
        ???

    }
  }

  override def read(): CommandResponse = ???

}
