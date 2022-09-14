package tech.yankun.sdoob

import better.files.File

object AppInfo {
  def appVersion: String = System.getProperty("prog.version")

  val copyrightStart = 2022

  def appDependencies: Seq[String] = {
    val home = File(System.getProperty("prog.home"))
    val appLib = home / "lib"
    appLib.list.filter(f => !f.name.contains("scala-library") && !f.name.contains("sdoob_")).map(f => f.pathAsString).toSeq
  }

  def appJar: String = {
    val home = File(System.getProperty("prog.home"))
    val appLib = home / "lib"
    appLib.list.filter(f => f.name.contains("sdoob_")).next().pathAsString
  }
}
