/*
 * Copyright (C) 2022  Yan Kun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
