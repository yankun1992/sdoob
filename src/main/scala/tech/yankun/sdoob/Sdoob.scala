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
