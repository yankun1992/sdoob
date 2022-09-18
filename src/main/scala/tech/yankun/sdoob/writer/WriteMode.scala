package tech.yankun.sdoob.writer

sealed trait WriteMode

object WriteMode {
  object INSERT extends WriteMode

  object UPDATE extends WriteMode

  object MERGE extends WriteMode
}


