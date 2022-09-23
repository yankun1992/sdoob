package tech.yankun.sdoob.args

import scopt.OParser
import tech.yankun.sdoob.AppInfo._

import java.time.LocalDate

object CmdParser {
  private val builder = OParser.builder[ArgsConfig]
  private val copyrightBetween = if (LocalDate.now().getYear == copyrightStart) s"${copyrightStart}" else s"${copyrightStart}-${LocalDate.now().getYear}"

  val parser: OParser[_, ArgsConfig] = {
    import builder._
    val dbInfo: scala.Seq[OParser[_, ArgsConfig]] = scala.Seq(
      opt[String]("connect").action((x, c) => c.copy(appArgs = c.appArgs.copy(connect = Some(x)))).text("Specify JDBC connect string"),
      opt[String]("connection-param-file").action((f, c) => c.copy()).text("Specify connection parameters file"),
      opt[String]("username").action((x, c) => c.copy(appArgs = c.appArgs.copy(username = Some(x)))).text("Set authentication username"),
      opt[String]("password").action((x, c) => c.copy(appArgs = c.appArgs.copy(password = Some(x)))).text("Set authentication password"),
      opt[String]("password-file").action((x, c) => c.copy()).text("Set authentication password file path"),
      opt[String]("table").action((x, c) => c.copy(appArgs = c.appArgs.copy(table = Some(x)))).text("Table to read")
    )
    val importArgs: scala.Seq[OParser[_, ArgsConfig]] = scala.Seq(
      opt[String]("query"),
      opt[String]("format")
    )
    OParser.sequence(
      programName("sdoob"),
      head("Running Sdoob", s"version ${appVersion} with Scala library ${scalaVersion}, Copyright ${copyrightBetween}, ${appAuthor}."),
      help("help").text("List available commands and options"),
      opt[Unit]("version").action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "version"))).text("Display version information"),
      // Spark args
      opt[String]("master")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(master = x)))
        .text("spark://host:port, mesos://host:port, yarn, k8s://https://host:port, or local (Default: local[1])."),
      opt[String]("deploy-mode")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`deploy-mode` = x)))
        .text("Whether to launch the driver program locally (\"client\") or on one of the worker machines inside the cluster (\"cluster\") (Default: client)."),
      opt[String]("name")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(name = Some(x))))
        .text("A name of your application."),
      opt[Seq[String]]("jars")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(jars = x))),
      opt[Map[String, String]]("conf")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(conf = x)))
        .text("Arbitrary Spark configuration property."),
      opt[String]("driver-memory")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`driver-memory` = x)))
        .text("Memory for driver (e.g. 1000M, 2G) (Default: 512M)."),
      opt[String]("driver-java-options")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`driver-java-options` = Some(x))))
        .text("Extra Java options to pass to the driver."),
      opt[String]("executor-memory")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`executor-memory` = x)))
        .text("Memory per executor (e.g. 1000M, 2G) (Default: 1G)."),
      opt[String]("proxy-user")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`proxy-user` = Some(x))))
        .text("User to impersonate when submitting the application. This argument does not work with --principal / --keytab."),
      opt[Unit]("verbose")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(verbose = true)))
        .text("Print additional debug output."),
      opt[Int]("driver-cores")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`driver-cores` = x)))
        .text("Number of cores used by the driver, only in cluster mode (Default: 1)."),
      opt[Unit]("supervise")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(supervise = true)))
        .text("If given, restarts the driver on failure."),
      opt[Int]("executor-cores")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`executor-cores` = x)))
        .text("Number of cores per executor. (Default: 1 in YARN mode, or all available cores on the worker in standalone mode)"),
      opt[String]("queue")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(queue = x)))
        .text("The YARN queue to submit to (Default: \"default\")."),
      opt[Int]("num-executors")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(`num-executors` = x)))
        .text("Number of executors to launch (Default: 2). If dynamic allocation is enabled, the initial number of executors will be at least NUM."),
      opt[String]("principal")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(principal = Some(x))))
        .text("Principal to be used to login to KDC, while running on secure HDFS."),
      opt[String]("keytab")
        .action((x, c) => c.copy(sparkArgs = c.sparkArgs.copy(principal = Some(x))))
        .text("The full path to the file that contains the keytab for the principal specified above."),

      // Sdoob app args
      cmd("create-hive-table")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "create-hive-table")))
        .text("Import a table definition into Hive")
        .children(
        ).children(dbInfo: _*),
      cmd("eval")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "eval")))
        .text("Evaluate a SQL statement and display the results")
        .children(),
      cmd("export")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "export")))
        .text("Export an HDFS directory to a database table")
        .children(dbInfo: _*),
      cmd("import")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "import")))
        .text("Import a table from a database to HDFS")
        .children(dbInfo: _*),
      cmd("import-all-tables")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "import-all-tables")))
        .text("Import tables from a database to HDFS")
        .children(),
      cmd("import-mainframe")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "import-mainframe")))
        .text("Import datasets from a mainframe server to HDFS").children(),
      cmd("job")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "job")))
        .text("Work with saved jobs").children(),
      cmd("list-databases")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "list-databases")))
        .text("List available databases on a server").children(),
      cmd("list-tables")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "list-tables")))
        .text("List available tables in a database")
        .children(),
      cmd("merge")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "merge")))
        .text("Merge results of incremental imports")
        .children(),
      cmd("version")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "version")))
        .text("Display version information"),
      cmd("help")
        .action((_, c) => c.copy(appArgs = c.appArgs.copy(command = "help")))
        .text("List available commands and options")
    )
  }
}
