package tech.yankun.sdoob.driver.mysql

import io.netty.buffer.{ByteBuf, Unpooled}
import tech.yankun.sdoob.driver.common._
import tech.yankun.sdoob.driver.mysql.codec.CommandCodec
import tech.yankun.sdoob.driver.mysql.command.AuthenticationCommandBase
import tech.yankun.sdoob.driver.{Client, Command, CommandResponse, SqlConnectOptions}

import java.net.SocketAddress
import java.nio.channels.SocketChannel
import java.nio.charset.Charset
import java.util

class MySQLClient(options: MySQLConnectOptions) extends Client(options) {

  import MySQLClient._

  private var charsetEncoding: Charset = _
  private var useAffectedRows: Boolean = _
  private var sslMode: SslMode = _
  private var serverRsaPublicKey: ByteBuf = _

  private var initialCapabilitiesFlags: Int = _

  private var pipeliningLimit: Int = options.getPipeliningLimit

  private val socket = SocketChannel.open()
  socket.connect(server)
  //  sendStartupMessage()

  // mysql codec inflight
  private val inflight: util.ArrayDeque[CommandCodec[_]] = new util.ArrayDeque[CommandCodec[_]]()


  override def initializeConfiguration(options: SqlConnectOptions): Unit = {

  }

  override def connect(remote: SocketAddress): Unit = {
    socket.connect(remote)
  }


  private def sendStartupMessage(username: String, password: String, database: String, collation: MySQLCollation,
                                 serverRsaPublicKey: ByteBuf, properties: Map[String, String], sslMode: SslMode,
                                 initialCapabilitiesFlags: Int, charsetEncoding: Charset,
                                 authenticationPlugin: MySQLAuthenticationPlugin): Unit = {
    ???
  }


  override def write(command: Command): Unit = {
    val codec = wrap(command)
    inflight.addLast(codec)

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

object MySQLClient {
  def wrap(cmd: Command): CommandCodec[_] = {
    cmd match {
      case base: AuthenticationCommandBase =>
        ???
      case CloseConnectionCommand =>
        ???
      case CloseCursorCommand(id, statement) =>
        ???
      case CloseStatementCommand(statement) =>
        ???
      case command: ExtendedQueryCommand =>
        ???
      case InitCommand(conn, username, password, database, properties) =>
        ???
      case base: QueryCommandBase =>
        ???
      case _ =>
        ???
    }
  }
}
