package tech.yankun.sdoob.driver.common

import tech.yankun.sdoob.driver.{Command, PreparedStatement}

case class CloseStatementCommand(statement: PreparedStatement) extends Command
