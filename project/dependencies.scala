import sbt._
import Keys._

object Dependencies {
  lazy val redshift = "com.amazon.redshift" % "redshift-jdbc42" % "1.2.1.1001"
  lazy val akkahttp = "com.typesafe.akka" %% "akka-http" % "10.0.9"
  lazy val config = "com.typesafe" % "config" % "1.3.1"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val tablesaw = "tech.tablesaw" % "tablesaw-core" % "0.9.0"
  lazy val log4j = "log4j" % "log4j" % "1.2.17"

  val dbtileDeps: Seq[ModuleID] = Seq(
    config,
    log4j,
    scalaTest
  )

  val webDeps: Seq[ModuleID] = Seq(
    config,
    akkahttp,
    tablesaw
  )
}
