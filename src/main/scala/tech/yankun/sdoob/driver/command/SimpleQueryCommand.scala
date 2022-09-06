package tech.yankun.sdoob.driver.command

case class SimpleQueryCommand(override val sql: String, singleton: Boolean = false,
                              override val autoCommit: Boolean = true)
  extends QueryCommandBase(autoCommit) {
}
