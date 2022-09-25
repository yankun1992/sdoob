package tech.yankun.sdoob.driver.command

import tech.yankun.sdoob.driver.codec.CommandCodec
import tech.yankun.sdoob.driver.{Client, ClientPool}

case class InitCommand[P <: ClientPool, C <: CommandCodec[_, _]](conn: Client[P, C],
                                                                 username: String,
                                                                 password: String,
                                                                 database: String,
                                                                 properties: Map[String, String])
  extends Command
