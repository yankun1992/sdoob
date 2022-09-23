package tech.yankun.sdoob

import better.files.File

object AppInfo {
  def appVersion: String = System.getProperty("prog.version")

  def appHome: String = System.getProperty("prog.home")

  val copyrightStart = 2022

  val appAuthor = "Yan Kun<1939810907@qq.com>"

  val scalaVersion = util.Properties.versionString

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
