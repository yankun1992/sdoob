package tech.yankun.sdoob.args

import tech.yankun.sdoob.AppInfo

case class ArgsConfig(
                       sparkArgs: SparkArgs = SparkArgs(),
                       appArgs: AppArgs = AppArgs()
                     )

case class AppArgs(
                    command: String = "",
                    connect: String = "",
                    username: String = "",
                    password: String = ""
                  ) {
  def toCommandLine: Array[String] = {
    val buffer = collection.mutable.ArrayBuffer.empty[String]
    buffer.append(command)
    buffer.append("--connect")
    buffer.append(connect)
    buffer.toArray
  }
}

/** Spark Args for SparkSubmit */
case class SparkArgs(
                      master: String = "local[1]",
                      `deploy-mode`: String = "client",
                      jars: Seq[String] = Seq.empty,
                      conf: Map[String, String] = Map.empty,
                      `driver-memory`: String = "512M",
                      `driver-java-options`: String = "",
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
    buffer.toArray
  }
}