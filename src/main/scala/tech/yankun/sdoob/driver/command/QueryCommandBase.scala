package tech.yankun.sdoob.driver.command

abstract class QueryCommandBase(val autoCommit: Boolean) extends Command {

  def sql(): String

}
