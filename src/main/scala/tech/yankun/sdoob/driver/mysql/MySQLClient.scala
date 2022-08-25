package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.{ByteBuf, Unpooled}
import tech.yankun.sdoob.driver.common._
import tech.yankun.sdoob.driver.{Client, Command, CommandResponse, SqlConnectOptions}

import java.net.SocketAddress
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

class MySQLClient(options: MySQLConnectOptions) extends Client(options) {
  private val socket = SocketChannel.open()
  private var charsetEncoding: Charset = _
  private var useAffectedRows: Boolean = _
  private var sslMode: SslMode = _
  private var serverRsaPublicKey: ByteBuf = _

  private var initialCapabilitiesFlags: Int = _


  override def initializeConfiguration(options: SqlConnectOptions): Unit = {

  }

  override def connect(remote: SocketAddress): Unit = {
    socket.connect(remote)
  }


  private def sendStartupMessage(): Unit = {
    ???
  }


  override def write(command: Command): Unit = {
    command match {
      case InitCommand(conn, username, password, database, properties) =>
        val buf = Unpooled.buffer(1024)
        buf.getBytes(0, socket, 100)
        ???
      case CloseConnectionCommand =>
        ???
      case SimpleQueryCommand() =>
        ???

    }
  }

  override def read(): CommandResponse = ???


  override def close(): Unit = ???

}
