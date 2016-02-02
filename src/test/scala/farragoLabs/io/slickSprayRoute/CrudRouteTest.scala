package farragoLabs.io.slickSprayRoute

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.routing.HttpService
import spray.http.StatusCodes._

import slick.driver.H2Driver.api._

import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.Await
import scala.concurrent.duration._

class FullTestKitExampleSpec extends Specification with Specs2RouteTest with HttpService {
  def actorRefFactory = system

  case class ExampleModel(id: Option[Int], value: String)

  implicit val exampleModelProtocol = jsonFormat2(ExampleModel)

  class ExampleTable(tag: Tag) extends Table[ExampleModel](tag, "Example") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def value = column[String]("value")

    def * = (id.?, value) <> (ExampleModel.tupled, ExampleModel.unapply)
  }

  implicit val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")
  implicit val exampleTable = TableQuery[ExampleTable]

  Await.result(db.run(exampleTable.schema.create), Duration(1, SECONDS))

  val exampleRoute = new CrudRoute[ExampleTable, ExampleModel](_.id, _.id.get)
}