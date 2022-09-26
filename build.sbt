// sbt-pack settings
enablePlugins(PackPlugin)
packMain := Map("sdoob" -> "tech.yankun.sdoob.SubmitApp")
packJvmOpts := Map("sdoob" -> Seq("-Xms124m", "-Xmx512m"))
packExtraClasspath := Map("sdoob" -> Seq("$SPARK_CLASSPATH", "${PROG_HOME}/conf"))
packJarListFile := Some("lib/jars.mf")

/**
 * cross build by build.py
 *
 * build.py will change [[packScalaVersion]] and [[packSparkVersion]] to version matrix.
 */
lazy val packScalaVersion = "2.11.8"
lazy val packSparkVersion = "2.4.8"

ThisBuild / organization := "tech.yankun"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := packScalaVersion

lazy val root = (project in file("."))
  .settings(
    name := "sdoob",

    libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0",
    libraryDependencies += "com.lihaoyi" %% "fastparse" % "2.3.3",
    libraryDependencies += "org.log4s" %% "log4s" % "1.10.0",
    libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1",
    libraryDependencies += "com.ongres.scram" % "client" % "2.1",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % packSparkVersion % "provided"

  )
