package io.farragoLabs

import slick.lifted.TableQuery
import spray.json.DefaultJsonProtocol._

/**
  * Created by Fydio on 2/9/16.
  */
package object slickSprayRoute {
  implicit lazy val exampleTable = TableQuery[TestTable]
  implicit lazy val exampleFormat = jsonFormat2(TestModel)
}
