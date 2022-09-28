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
import buildinfo.BuildInfo

import java.time.LocalDate

/** build info for this application */
object AppInfo {

  private val RE_COPYRIGHT_BETWEEN = "(\\d+)-(\\d+)".r
  private val RE_COPYRIGHT = "(\\d+)".r

  val name: String = BuildInfo.name

  val appVersion: String = BuildInfo.version

  def appHome: String = Option(System.getProperty("prog.home")).getOrElse("target/pack")

  val copyright: String = BuildInfo.copyright match {
    case RE_COPYRIGHT_BETWEEN(start, end) => BuildInfo.copyright
    case RE_COPYRIGHT(start) =>
      if (LocalDate.now().getYear == start.toInt) start else s"$start-${LocalDate.now().getYear}"
    case _ =>
      if (LocalDate.now().getYear == 2022) "2022" else s"2022-${LocalDate.now().getYear}"
  }

  val appAuthor: String = BuildInfo.author

  val scalaVersion: String = BuildInfo.scalaVersion

  val license: String = BuildInfo.license

  override def toString: String =
    s"version ${appVersion} with Scala library ${scalaVersion} and compatible with spark ${BuildInfo.spark}, " +
      s"Copyright (C) ${copyright} ${appAuthor} under ${license} license"

  private def dependencies: Seq[File] = (File(appHome) / "lib").list.toSeq

  /**
   * dependencies for pass to spark-submit --jars
   *
   * @return dependencies
   */
  def appDependencies: Seq[String] =
    dependencies.filter(f => !f.name.contains("scala-library") && !f.name.contains(s"${name}_")).map(f => f.pathAsString)

  /** main class jar for running spark application */
  def appJar: String = dependencies.filter(f => f.name.contains(s"${name}_")).head.pathAsString
}
