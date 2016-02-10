package io.farragoLabs

import spray.json.DefaultJsonProtocol._

import slick.lifted.TableQuery

package object crudRoute {
  implicit lazy val exampleTable = TableQuery[TestTable]
  implicit lazy val exampleFormat = jsonFormat2(TestModel)
}