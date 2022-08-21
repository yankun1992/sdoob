lazy val scala212 = "2.12.0"
lazy val scala211 = "2.11.0"
lazy val scala213 = "2.13.0"
lazy val supportedScalaVersions = List(scala212, scala211, scala213)

ThisBuild / organization := "tech.yankun"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala211

lazy val root = (project in file("."))
  .settings(
    name := "sdoob",
    crossScalaVersions := supportedScalaVersions,

    libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1",

    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 12)) =>
          List("org.apache.spark" %% "spark-sql" % "2.4.5" % "provided")
        case Some((2, 13)) =>
          List("org.apache.spark" %% "spark-sql" % "3.2.0" % "provided")
        case Some((2, 11)) =>
          List("org.apache.spark" %% "spark-sql" % "2.3.0" % "provided")
        case _ => Nil
      }
    },
  )
