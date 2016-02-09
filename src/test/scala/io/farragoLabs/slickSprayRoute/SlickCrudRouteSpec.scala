package io.farragoLabs.slickSprayRoute

import org.scalatest._

import slick.driver.H2Driver
import H2Driver.api._

import spray.json._
import DefaultJsonProtocol._
import spray.routing._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by admin on 2/6/16.
  */
class SlickCrudRouteSpec extends CrudRouteSpec with BeforeAndAfterAll {
  val timeout = Duration(1, SECONDS)

  implicit val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")
  implicit val exampleTable = TableQuery[ExampleTable]
  implicit override val exampleFormat = jsonFormat2(ExampleModel)

  val driverProvider = new SlickCrudDriverProvider[H2Driver.type](H2Driver)

  val testRoute: Route = {
    new CrudRoute[ExampleModel](driverProvider[ExampleModel, ExampleTable](_.id, _.id.get))
  }

  override def beforeAll() = {
    Await.result(db.run(exampleTable.schema.create), timeout)
  }

  override def afterAll() = {
    Await.result(db.run(exampleTable.schema.drop), timeout)
  }
}
