package tech.yankun.sdoob

import better.files.File
import org.apache.spark.deploy.SdoobSubmit
import org.log4s.getLogger
import scopt.OParser
import tech.yankun.sdoob.AppInfo.appVersion
import tech.yankun.sdoob.args.{ArgsConfig, CmdParser}

import java.util.Properties

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
            val submitCommandLine = generateSubmitArgs(argsConfig)
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
    if (properties.containsKey("verbose") && properties.getProperty("verbose").asInstanceOf[Boolean]) config = config.copy(sparkArgs = config.sparkArgs.copy(verbose = true))
    if (properties.containsKey("driver-cores")) config = config.copy(sparkArgs = config.sparkArgs.copy(`driver-cores` = properties.getProperty("driver-cores").toInt))
    if (properties.containsKey("supervise") && properties.getProperty("supervise").asInstanceOf[Boolean]) config = config.copy(sparkArgs = config.sparkArgs.copy(supervise = true))
    if (properties.containsKey("executor-cores")) config = config.copy(sparkArgs = config.sparkArgs.copy(`executor-cores` = properties.getProperty("executor-cores").toInt))
    if (properties.containsKey("queue")) config = config.copy(sparkArgs = config.sparkArgs.copy(queue = properties.getProperty("queue")))
    if (properties.containsKey("num-executors")) config = config.copy(sparkArgs = config.sparkArgs.copy(`num-executors` = properties.getProperty("num-executors").toInt))

    config
  }
}
