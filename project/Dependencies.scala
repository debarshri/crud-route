import sbt._
import Keys._

object Dependencies {
  val akkaVersion = "2.3.9"
  val sprayVersion = "1.3.3"
  val slickVersion = "3.1.1"

  val coreDeps = Seq(
    "org.slf4j" % "slf4j-nop" % "1.6.4"
  )

  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
  )

  val sprayDeps = Seq(
    "io.spray" %% "spray-routing-shapeless2" % sprayVersion,
    "io.spray" %% "spray-http" % sprayVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-json" % "1.3.2"
  ) ++ akkaDeps

  val slickDeps = Seq(
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "com.zaxxer" % "HikariCP" % "2.4.3",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0"
  )

  val mysqlDeps = Seq(
    "mysql" % "mysql-connector-java" % "5.1.38"
  )

  val h2Deps = Seq(
    "com.h2database" % "h2" % "1.4.191"
  )

  val testDeps = Seq(
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}
