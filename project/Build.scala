import sbt._
import Keys._

object BuildSettings {
  val ScalaVersion = "2.11.1"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.softwaremill.scalaval",
    version := "0.1-SNAPSHOT",
    scalaVersion := ScalaVersion
  )
}

object ScalavalBuild extends Build {
  import BuildSettings._

  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

  lazy val scalaval: Project = Project(
    "scalaval",
    file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
      )
    )
  )

}
