package io.farragoLabs.crudRoute.slickDriver

import org.scalatest.BeforeAndAfterAll
import io.farragoLabs.crudRoute.{CrudDriverSpec, TestModel}

import scala.concurrent.Await
import scala.concurrent.duration._

import slick.driver.H2Driver
import H2Driver.api._

class SlickCrudDriver extends CrudDriverSpec with BeforeAndAfterAll {

  def timeout = Duration(1, SECONDS)
  implicit val db = Database.forURL("jdbc:h2:mem:crudDriverTest;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")

  val driverProvider = new SlickCrudDriverProvider[H2Driver.type](H2Driver)
  def driver = driverProvider[TestModel, TestTable](_.id, _.id.get)


  override def beforeAll() = {
    Await.result(db.run(exampleTable.schema.create), timeout)
  }

  override def afterAll() = {
    Await.result(db.run(exampleTable.schema.drop), timeout)
  }
}
