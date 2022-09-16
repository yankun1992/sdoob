package tech.yankun.sdoob

import org.log4s.getLogger
import scopt.OParser
import tech.yankun.sdoob.CommandHandler._
import tech.yankun.sdoob.args.{ArgsConfig, CmdParser}

object Sdoob extends App {
  private val logger = getLogger
  logger.warn(s"sdoob app command line args is ${args.mkString("[", " ", "]")}")
  OParser.parse(CmdParser.parser, args, ArgsConfig()) match {
    case Some(argsConfig: ArgsConfig) =>
      argsConfig.appArgs.command match {
        case "export" =>
          handleExport(argsConfig)
        case "import" =>
          handleImport(argsConfig)
        case unknown =>
          throw new IllegalArgumentException(s"Unsupported command [${unknown}]")
      }
    case None =>
  }
}
