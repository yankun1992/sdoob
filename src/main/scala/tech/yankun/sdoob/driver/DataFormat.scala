package tech.yankun.sdoob.driver

trait DataFormat

object DataFormat {
  object TEXT extends DataFormat

  object BINARY extends DataFormat

  def valueOf(id: Int): DataFormat = if (id == 0) TEXT else BINARY
}
