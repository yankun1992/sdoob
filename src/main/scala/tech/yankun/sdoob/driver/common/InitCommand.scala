package tech.yankun.sdoob.driver.common

import tech.yankun.sdoob.driver.{Client, Command}

case class InitCommand(conn: Client, username: String, password: String, database: String,
                       properties: Map[String, String]) extends Command


