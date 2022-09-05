package tech.yankun.sdoob.driver.command

abstract class QueryCommandBase extends Command {

  def sql(): String

}
