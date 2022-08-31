package tech.yankun.sdoob.driver.mysql.command

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.mysql.{MySQLAuthenticationPlugin, MySQLClient, MySQLCollation, SslMode}

import java.nio.charset.Charset

class InitialHandshakeCommand(val client: MySQLClient,
                              username: String,
                              password: String,
                              database: String,
                              collation: MySQLCollation,
                              serverRsaPublicKey: ByteBuf,
                              connectionAttributes: Map[String, String],
                              val sslMode: SslMode,
                              val initialCapabilitiesFlags: Int,
                              val charsetEncoding: Charset,
                              val authenticationPlugin: MySQLAuthenticationPlugin
                             ) extends AuthenticationCommandBase(username, password, database, collation, serverRsaPublicKey, connectionAttributes)
