package farragoLabs.io.slickSprayRoute

import org.specs2.execute.{Result, AsResult}
import org.specs2.mutable.Specification
import org.specs2.specification._
import spray.testkit.Specs2RouteTest
import spray.routing.HttpService
import spray.http.StatusCodes._

import slick.driver.H2Driver.api._

import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

case class ExampleModel(id: Option[Int], value: String)

class ExampleTable(tag: Tag) extends Table[ExampleModel](tag, "Example") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def value = column[String]("value")

  def * = (id.?, value) <> (ExampleModel.tupled, ExampleModel.unapply)
}

trait DatabaseContext extends AroundEach {

}

class CrudRouteSpec extends Specification with Specs2RouteTest with HttpService {
  sequential
  def actorRefFactory = system
  val timeout = Duration(1, SECONDS)

  implicit val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")
  implicit val exampleTable = TableQuery[ExampleTable]
  implicit val exampleFormat = jsonFormat2(ExampleModel)

  val exampleRoute = new CrudRoute[ExampleTable, ExampleModel](_.id, _.id.get)

  Try { tearDownDb() }
  setupDb()

  "CrudRoute" >> {
    "Get an empty jsonList for an empty table" in {
      Get() ~> exampleRoute ~> check {
        responseAs[List[Int]] === List()
      }
    }

    "Creating a new model should" >> {
      "return a new Id" >> {
        Put("/", ExampleModel(None, "First!")) ~> exampleRoute ~> check {
          responseAs[String] === "1"
        }
      }

      "make the model available by id" >> {
        Get("/1") ~> exampleRoute ~> check {
          responseAs[ExampleModel] === ExampleModel(Some(1), "First!")
        }
      }

      "show the model in the listing" >> {
        Get() ~> exampleRoute ~> check {
          responseAs[List[Int]] === List(1)
        }
      }
    }

    "Updating an existing model should" >> {
      "give a 200-OK" >> {
        Post("/", ExampleModel(Some(1), "1st")) ~> exampleRoute ~> check {
          status === OK
        }
      }

      "make the new model available" >> {
        Get("/1") ~> exampleRoute ~> check {
          responseAs[ExampleModel] === ExampleModel(Some(1), "1st")
        }
      }
    }

    "Deleting an existing model should" >> {
      "give an Ok" >> {
        Delete("/1") ~> exampleRoute ~> check {
          status === OK
        }
      }
      "make the model unavailable" >> {
        Get("/1") ~> exampleRoute ~> check {
          status === NotFound
        }
      }
    }
  }

  def before = {
    Try { tearDownDb() }
    setupDb()
  }

  def after = tearDownDb()

  def setupDb(): Unit = {
    Await.result(db.run(exampleTable.schema.create), timeout)
  }

  def tearDownDb(): Unit = {
    Await.result(db.run(exampleTable.schema.drop), timeout)
  }
}