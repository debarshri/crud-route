import sbt._
import Keys._

object SlickSprayCrudRoute extends Build {

  import Dependencies._

  lazy val root = Project(
    id = "slick-spray-route",
    base = file("."),
    settings = Seq(
      organization := "io.farragoLabs",
      version := "1.0.0-SNAPSHOT",
      licenses := Seq(),
      homepage := Some(url("http://github.com/FarragoLabs/slick-spray-route")),

      scalaVersion := "2.11.7",
      resolvers := Seq(
        "spray repo" at "http://repo.spray.io",
        Resolver.typesafeRepo("releases"),
        Resolver.sonatypeRepo("public")
      ),
      libraryDependencies ++= coreDeps ++ sprayDeps ++ slickDeps ++ h2Deps ++ testDeps,

      fork in test := true
    )
  )

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <scm>
        <url>git@github.com/FarragoLabs/slick-spray-route.git</url>
        <connection>scm:git:git@github.com/FarragoLabs/slick-spray-route.git</connection>
      </scm>
      <developers>
        <developer>
          <id>fmsbeekmans</id>
          <name>Ferdy Moon Soo Beekmans</name>
        </developer>
        <developer>
          <id>dbasak</id>
          <name>Debarshi Basak</name>
        </developer>
      </developers>
      )
  )
}
