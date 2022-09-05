package tech.yankun.sdoob.driver.command

import tech.yankun.sdoob.driver.PreparedStatement

case class CloseCursorCommand(id: String, statement: PreparedStatement) extends Command
