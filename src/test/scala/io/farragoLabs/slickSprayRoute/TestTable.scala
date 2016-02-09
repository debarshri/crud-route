package io.farragoLabs.slickSprayRoute

import slick.lifted.Tag
import slick.driver.H2Driver.api._

case class TestModel(id: Option[Int], value: String)

object TestModel extends ((Option[Int], String) => TestModel) {
  def apply(value: String): TestModel = TestModel(None, value)
}

class TestTable(tag: Tag) extends Table[TestModel](tag, "Test") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def value = column[String]("value")

  def * = (id.?, value) <> (TestModel.tupled, TestModel.unapply)
}
