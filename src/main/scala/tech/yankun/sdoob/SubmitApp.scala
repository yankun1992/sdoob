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
import org.apache.spark.deploy.SdoobSubmit
import org.log4s.getLogger
import scopt.OParser
import tech.yankun.sdoob.AppInfo.appVersion
import tech.yankun.sdoob.args.{ArgsConfig, CmdParser}
import tech.yankun.sdoob.utils.Utils

import java.util.Properties
import scala.language.implicitConversions

/**
 * Command line interface for the Sdoob launcher. Used internally by Sdoob scripts.
 */
object SubmitApp {
  private val logger = getLogger

  def main(args: Array[String]): Unit = {
    // init submit args
    val config = initDefaultArgsConfig()
    OParser.parse(CmdParser.parser, args, config) match {
      case Some(argsConfig: ArgsConfig) =>
        argsConfig.appArgs.command match {
          case "version" =>
            println(s"version ${appVersion}")
          case "help" =>
            OParser.parse(CmdParser.parser, Array("--help"), ArgsConfig())
          case _ =>
            val config = checkAndCorrectArgs(argsConfig)
            val submitCommandLine = generateSubmitArgs(config)
            logger.warn(s"spark submit command line args is ${submitCommandLine.mkString("[", " ", "]")}")
            SdoobSubmit.submit(submitCommandLine)
        }
      case None =>
        logger.error("command args parser error")
    }
  }

  private def generateSubmitArgs(argsConfig: ArgsConfig): Array[String] = {
    argsConfig.sparkArgs.toCommandLine ++ Array("--class", "tech.yankun.sdoob.Sdoob", AppInfo.appJar) ++
      argsConfig.appArgs.toCommandLine ++ argsConfig.getSparkSubmitLine
  }

  private def initDefaultArgsConfig(): ArgsConfig = {
    // load submit-default.properties
    val submitDefault = (File(AppInfo.appHome) / "conf" / "submit-default.properties").createFileIfNotExists(true)
    val properties = new Properties()
    properties.load(submitDefault.newInputStream)

    // initial ArgsConfig by submit-default.properties
    var config = ArgsConfig()
    if (properties.containsKey("master")) config = config.copy(sparkArgs = config.sparkArgs.copy(master = properties.getProperty("master")))
    if (properties.containsKey("deploy-mode")) config = config.copy(sparkArgs = config.sparkArgs.copy(`deploy-mode` = properties.getProperty("deploy-mode")))
    if (properties.containsKey("driver-memory")) config = config.copy(sparkArgs = config.sparkArgs.copy(`driver-memory` = properties.getProperty("driver-memory")))
    if (properties.containsKey("executor-memory")) config = config.copy(sparkArgs = config.sparkArgs.copy(`executor-memory` = properties.getProperty("executor-memory")))
    if (properties.containsKey("verbose") && Utils.string2Boolean(properties.getProperty("verbose"))) config = config.copy(sparkArgs = config.sparkArgs.copy(verbose = true))
    if (properties.containsKey("driver-cores")) config = config.copy(sparkArgs = config.sparkArgs.copy(`driver-cores` = properties.getProperty("driver-cores").toInt))
    if (properties.containsKey("supervise") && Utils.string2Boolean(properties.getProperty("supervise"))) config = config.copy(sparkArgs = config.sparkArgs.copy(supervise = true))
    if (properties.containsKey("executor-cores")) config = config.copy(sparkArgs = config.sparkArgs.copy(`executor-cores` = properties.getProperty("executor-cores").toInt))
    if (properties.containsKey("queue")) config = config.copy(sparkArgs = config.sparkArgs.copy(queue = properties.getProperty("queue")))
    if (properties.containsKey("num-executors")) config = config.copy(sparkArgs = config.sparkArgs.copy(`num-executors` = properties.getProperty("num-executors").toInt))

    config
  }

  private def checkAndCorrectArgs(argsConfig: ArgsConfig): ArgsConfig = {
    var config = argsConfig
    argsConfig.appArgs.command match {
      case "import" =>
        if (argsConfig.sparkArgs.master.startsWith("local")) config = config.copy(sparkArgs = config.sparkArgs.copy(master = "local[1]"))
        if (argsConfig.sparkArgs.`executor-cores` > 1 || argsConfig.sparkArgs.`num-executors` > 1) {
          logger.warn("import dataset form rdbms to hdfs only support one thread")
          config = config.copy(sparkArgs = config.sparkArgs.copy(`executor-cores` = 1, `num-executors` = 1))
        }
      case "export" =>
    }
    config
  }
}
