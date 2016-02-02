import sbt._
import Keys._

object FarragoSiteBuild extends Build {

  import Dependencies._

  lazy val root = Project(
    id = "slick-spray-route",
    base = file("."),
    settings = Seq(
      organization := "farragoLabs.io",
      version := "1.0.0-SNAPSHOT",
      scalaVersion := "2.11.7",
      resolvers := Seq(
        "spray repo" at "http://repo.spray.io",
        Resolver.typesafeRepo("releases"),
        Resolver.sonatypeRepo("public")
      ),
      libraryDependencies ++= coreDeps ++ sprayDeps ++ slickDeps ++ mysqlDeps ++ testDeps
    )
  )
}
