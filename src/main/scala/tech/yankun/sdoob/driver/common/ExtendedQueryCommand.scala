package tech.yankun.sdoob.driver.common

import tech.yankun.sdoob.driver.Command

class ExtendedQueryCommand extends Command {

}

object ExtendedQueryCommand {
  def createQuery(): ExtendedQueryCommand = {
    ???
  }

  def createQuery(sql: String): ExtendedQueryCommand = {
    ???
  }

  def createBatch(sql: String): ExtendedQueryCommand = {
    ???
  }

}