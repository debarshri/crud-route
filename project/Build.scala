import sbt._
import Keys._

object FarragoSiteBuild extends Build {

  import Dependencies._

  lazy val root = Project(
    id = "slick-spray-route",
    base = file("."),
    settings = Seq(
      organization := "farragoLabs.io",
      scalaVersion := "2.11.7",
      resolvers := Seq(
        "spray repo" at "http://repo.spray.io",
        Resolver.typesafeRepo("releases")
      ),
      libraryDependencies ++= coreDeps ++ sprayDeps ++ slickDeps ++ mysqlDeps
    )
  )
}
