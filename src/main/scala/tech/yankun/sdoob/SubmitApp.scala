package tech.yankun.sdoob

import org.apache.spark.deploy.SdoobSubmit
import org.log4s.getLogger
import scopt.OParser
import tech.yankun.sdoob.AppInfo.appVersion
import tech.yankun.sdoob.args.{ArgsConfig, CmdParser}

/**
 * Command line interface for the Sdoob launcher. Used internally by Sdoob scripts.
 */
object SubmitApp extends App {
  private val logger = getLogger
  OParser.parse(CmdParser.parser, args, ArgsConfig()) match {
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

  private def generateSubmitArgs(argsConfig: ArgsConfig): Array[String] = {
    argsConfig.sparkArgs.toCommandLine ++ Array("--class", "tech.yankun.sdoob.Sdoob", AppInfo.appJar) ++
      argsConfig.appArgs.toCommandLine ++ argsConfig.getSparkSubmitLine
  }
}
