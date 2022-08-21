package tech.yankun.sdoob.driver.common

import tech.yankun.sdoob.driver.{Command, PreparedStatement}

case class CloseCursorCommand(id: String, statement: PreparedStatement) extends Command
