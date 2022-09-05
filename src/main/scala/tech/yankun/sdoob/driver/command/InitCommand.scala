package tech.yankun.sdoob.driver.command

import tech.yankun.sdoob.driver.Client

case class InitCommand(conn: Client, username: String, password: String, database: String,
                       properties: Map[String, String]) extends Command
