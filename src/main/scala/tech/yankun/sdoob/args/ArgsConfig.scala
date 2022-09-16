package tech.yankun.sdoob.args

import tech.yankun.sdoob.AppInfo

case class ArgsConfig(
                       sparkArgs: SparkArgs = SparkArgs(),
                       appArgs: AppArgs = AppArgs()
                     ) {
  def getSparkSubmitLine: Array[String] = sparkArgs.toCommandLine

  def getSdoobLine: Array[String] = appArgs.toCommandLine
}

case class AppArgs(
                    command: String = "help",
                    connect: Option[String] = None,
                    username: Option[String] = None,
                    password: Option[String] = None,
                    table: Option[String] = None,
                    append: Boolean = false,
                    format: String = "parquet",
                    `boundary-query`: Option[String] = None,
                    columns: Seq[String] = Seq.empty,
                    `delete-target-dir`: Boolean = false,
                    query: Option[String] = None,
                    `split-by`: Option[String] = None,
                    `split-limit`: Option[String] = None,
                    `target-dir`: Option[String] = None,
                    `temporary-rootdir`: String = "_sdoob",
                    `warehouse-dir`: Option[String] = None,
                    compress: Boolean = false,
                    `compression-codec`: String = "gzip",
                    `null-string`: Option[String] = None,
                    `null-non-string`: Option[AnyVal] = None,
                    `fields-terminated-by`: Option[String] = None,
                    `lines-terminated-by`: Option[String] = None,
                    `mysql-delimiters`: Boolean = false
                  ) {
  def toCommandLine: Array[String] = {
    val buffer = collection.mutable.ArrayBuffer.empty[String]
    buffer.append(command)
    connect match {
      case Some(x) =>
        buffer.append("--connect")
        buffer.append(x)
      case None =>
    }

    buffer.toArray
  }
}

/** Spark Args for SparkSubmit */
case class SparkArgs(
                      master: String = "local[1]",
                      `deploy-mode`: String = "client",
                      name: Option[String] = None,
                      jars: Seq[String] = Seq.empty,
                      conf: Map[String, String] = Map.empty,
                      `driver-memory`: String = "512M",
                      `driver-java-options`: Option[String] = None,
                      `executor-memory`: String = "1G",
                      `proxy-user`: Option[String] = None,
                      verbose: Boolean = false,
                      `driver-cores`: Int = 1,
                      supervise: Boolean = false,
                      `executor-cores`: Int = 1,
                      queue: String = "default",
                      `num-executors`: Int = 1,
                      principal: Option[String] = None,
                      keytab: Option[String] = None
                    ) {
  def toCommandLine: Array[String] = {
    val buffer = collection.mutable.ArrayBuffer.empty[String]
    buffer.append("--master")
    buffer.append(master)
    if (!master.startsWith("local")) {
      buffer.append("--deploy-mode")
      buffer.append(`deploy-mode`)
    }
    buffer.append("--jars")
    buffer.append((AppInfo.appDependencies ++ jars).mkString(","))
    if (conf.nonEmpty) {
      buffer.append("--conf")
      buffer.append(conf.map { case (key, value) => s"$key=$value" }.mkString(","))
    }
    buffer.append("--driver-memory")
    buffer.append(`driver-memory`)
    `driver-java-options` match {
      case Some(value) =>
        buffer.append("--driver-java-options")
        buffer.append(value)
      case None =>
    }
    buffer.append("--executor-memory")
    buffer.append(`executor-memory`)
    `proxy-user` match {
      case Some(value) =>
        buffer.append("--proxy-user")
        buffer.append(value)
      case None =>
    }
    if (verbose) {
      buffer.append("--verbose")
    }
    if (`deploy-mode` == "cluster") {
      buffer.append("--driver-cores")
      buffer.append(`driver-cores`.toString)
    }
    if (supervise && (master.startsWith("spark") || master.startsWith("mesos")) && `deploy-mode` == "cluster") {
      buffer.append("--supervise")
    }
    if (master.startsWith("yarn") || master.startsWith("spark")) {
      buffer.append("--executor-cores")
      buffer.append(`executor-cores`.toString)
    }
    if (master.startsWith("yarn")) {
      buffer.append("--queue")
      buffer.append(queue)
      buffer.append("--num-executors")
      buffer.append(`num-executors`.toString)
      principal match {
        case Some(value) =>
          buffer.append("--principal")
          buffer.append(value)
        case None =>
      }
      keytab match {
        case Some(value) =>
          buffer.append("--keytab")
          buffer.append(value)
        case None =>
      }
    }
    buffer.toArray
  }
}