package io.farragoLabs.slickSprayRoute

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import slick.driver.H2Driver.api._
import slick.lifted.Tag
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing.Route
import spray.testkit.ScalatestRouteTest

abstract class CrudRouteSpec extends FreeSpec with Matchers with ScalatestRouteTest with ScalaFutures {

  def actorRefFactory = system

  implicit val exampleFormat = jsonFormat2(TestModel)

  def testRoute: CrudRoute

  "CrudRoute" - {
    "Get an empty jsonList for an empty table" in {
      Get() ~> testRoute ~> check {
        responseAs[List[Int]] shouldEqual List()
      }
    }

    "Creating a new model should" - {
      "return a new Id" in {
        Put("/", TestModel(None, "First!")) ~> testRoute ~> check {
          responseAs[String] shouldEqual "1"
        }
      }

      "make the model available by id" in {
        Get("/1") ~> testRoute ~> check {
          responseAs[TestModel] shouldEqual TestModel(Some(1), "First!")
        }
      }

      "show the model in the listing" in {
        Get() ~> testRoute ~> check {
          responseAs[List[Int]] shouldEqual List(1)
        }
      }
    }

    "Updating an existing model should" - {
      "give a 200-OK" in {
        Post("/", TestModel(Some(1), "1st")) ~> testRoute ~> check {
          status shouldEqual OK
        }
      }

      "make the new model available" in {
        Get("/1") ~> testRoute ~> check {
          responseAs[TestModel] shouldEqual TestModel(Some(1), "1st")
        }
      }
    }

    "Deleting an existing model should" - {
      "give an Ok" in {
        Delete("/1") ~> testRoute ~> check {
          status shouldEqual OK
        }
      }

      "make the model unavailable" in {
        Get("/1") ~> testRoute ~> check {
          status shouldEqual NotFound
        }
      }

      "remove the model from the listing" in {
        Get() ~> testRoute ~> check {
          responseAs[List[Int]] shouldEqual List()
        }
      }
    }

    "Non-existant models" - {
      "Are not available" in {
        Get("/1") ~> testRoute ~> check {
          status shouldEqual NotFound
        }
      }

      "Are not updatable" in {
        Post("/", TestModel(Some(1), "Non-sense!")) ~> testRoute ~> check {
          status shouldEqual NotFound
        }
      }

      "Are not deletable" in {
        Delete("/1") ~> testRoute ~> check {
          status shouldEqual NotFound
        }
      }
    }

    "Inserting a model a given id" - {
      val withId = TestModel(Some(1234), "New Model")

      "Is allowed but the id is ignored" in {
        Put("/", withId) ~> testRoute ~> check {
          status shouldEqual OK
          responseAs[String] shouldEqual "2"
        }
      }

      "Even if the id is taken" in {
        Put("/", withId) ~> testRoute ~> check {
          status shouldEqual OK
          responseAs[String] shouldEqual "3"
        }
      }
    }
  }
}