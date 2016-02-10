package io.farragoLabs.crudRoute

import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

import spray.routing._

import slick.driver.H2Driver
import H2Driver.api._

class SlickCrudRouteSpec extends CrudRouteSpec with BeforeAndAfterAll {

  val timeout = Duration(1, SECONDS)

  implicit val db = Database.forURL("jdbc:h2:mem:crudRouteTest;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")

  val driverProvider = new SlickCrudDriverProvider[H2Driver.type](H2Driver)

  val testRoute: Route = {
    new CrudRoute[TestModel](driverProvider[TestModel, TestTable](_.id, _.id.get))
  }

  override def beforeAll() = {
    Await.result(db.run(exampleTable.schema.create), timeout)
  }

  override def afterAll() = {
    Await.result(db.run(exampleTable.schema.drop), timeout)
  }
}
