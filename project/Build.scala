import sbt._
import Keys._

object SlickSprayCrudRoute extends Build {

  import Dependencies._

  lazy val root = Project(
    id = "crud-route",
    base = file("."),
    settings = commonSettings ++ testSettings ++
      Seq(
        libraryDependencies ++= coreDeps ++ sprayDeps ++ testDeps))

  lazy val slickDriver = Project(
    id = "crud-route-slick-driver",
    base = file("./slickDriver"),
    settings = commonSettings ++ testSettings ++
      Seq(
        libraryDependencies ++= coreDeps ++ sprayDeps ++ slickDeps ++ h2Deps ++ testDeps)
  ) dependsOn(root % "compile->compile;test->test")

  lazy val commonSettings = Seq(
    organization := "io.farragoLabs",
    version := "1.0.0-SNAPSHOT",
    licenses := Seq(),
    homepage := Some(url("http://github.com/FarragoLabs/crud-route")),

    scalaVersion := "2.11.7",
    resolvers := Seq(
      "spray repo" at "http://repo.spray.io",
      Resolver.typesafeRepo("releases"),
      Resolver.sonatypeRepo("public")
    )
  )

  lazy val testSettings = Seq(
    fork in Test := true
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
