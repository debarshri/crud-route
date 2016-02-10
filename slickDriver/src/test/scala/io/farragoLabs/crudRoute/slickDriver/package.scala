package io.farragoLabs.crudRoute

import spray.json.DefaultJsonProtocol._

import slick.lifted.TableQuery

package object slickDriver {
  implicit lazy val exampleTable = TableQuery[TestTable]
  implicit lazy val exampleFormat = jsonFormat2(TestModel)
}
