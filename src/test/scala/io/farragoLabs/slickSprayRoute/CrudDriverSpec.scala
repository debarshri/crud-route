package io.farragoLabs.slickSprayRoute

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

abstract class CrudDriverSpec extends FreeSpec with Matchers with ScalaFutures {

  def driver: CrudDriver[TestModel]

  "Driver" - {
    "Empty driver should give an empty list" in {
      whenReady(driver listModelIds) { ids => {
        ids shouldBe empty
      }}
    }

    "With a newly created model" - {
      val newModel = TestModel("Newly made")

      "Return a new id" in {
        whenReady(driver createModel newModel) { result =>
          result should equal (1)
        }
      }


      "Should add the new id to the listing" in {
        whenReady(driver listModelIds) { ids =>
          ids should contain(1)
        }
      }

      "Trying to read a model" - {
        "Should return an existing model" in {
          whenReady(driver readModelById 1) { readModel =>
            readModel should contain(TestModel(Option(1), "Newly made"))
          }
        }

        "Should return None when it does not exist" in {
          whenReady(driver readModelById (2)) { readModel =>
            readModel shouldBe empty
          }
        }
      }

      "Trying to update a model" - {
        val updatedModel = TestModel(Some(1), "Updated")
        "Should return true for an existing model" in {
          whenReady(driver updateModel (updatedModel)) { isUpdated =>
            isUpdated shouldBe true
          }
        }

        "Should make the updated version available" in {
          whenReady(driver readModelById (1)) { readModel =>
            readModel should contain(updatedModel)
          }
        }

        "Or return false if the model doesn't exist" in {
          whenReady(driver updateModel(TestModel(Some(3), "I'm new here!"))) { isUpdated =>
            isUpdated shouldBe false
          }
        }
      }

      "Trying to delete a model" - {
        "Should return true if the model exists" in {
          whenReady(driver deleteModel(1)) { isDeleted =>
            isDeleted shouldBe true
          }
        }

        "Should return false if the model doesn't exists" in {
          whenReady(driver deleteModel(23)) { isDeleted =>
            isDeleted shouldBe false
          }
        }
      }
    }
  }
}
