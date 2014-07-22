import sbt._
import Keys._

object BuildSettings {
  val ScalaVersion = "2.11.1"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.softwaremill.scalaval",
    version := "0.1-SNAPSHOT",
    scalaVersion := ScalaVersion,
    // Sonatype OSS deployment
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra :=
      <scm>
        <url>git@github.com:mostr/scalaval.git</url>
        <connection>scm:git:git@github.com:mostr/scalaval.git</connection>
      </scm>
        <developers>
          <developer>
            <id>mostr</id>
            <name>Michał Ostruszka</name>
            <url>http://michalostruszka.pl</url>
          </developer>
        </developers>,
    licenses := ("Apache2", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
    homepage := Some(new java.net.URL("http://michalostruszka.pl"))
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
