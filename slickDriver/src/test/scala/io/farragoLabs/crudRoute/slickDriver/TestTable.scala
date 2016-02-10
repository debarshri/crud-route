package io.farragoLabs.crudRoute.slickDriver

import slick.lifted.Tag
import slick.driver.H2Driver.api._

import io.farragoLabs.crudRoute.TestModel

class TestTable(tag: Tag) extends Table[TestModel](tag, "Test") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def value = column[String]("value")

  def * = (id.?, value) <> (TestModel.tupled, TestModel.unapply)
}
