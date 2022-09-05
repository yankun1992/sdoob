package tech.yankun.sdoob.driver.command

import tech.yankun.sdoob.driver.PreparedStatement

case class CloseStatementCommand(statement: PreparedStatement) extends Command
