package io.farragoLabs.crudRoute

case class TestModel(id: Option[Int], value: String)

object TestModel extends ((Option[Int], String) => TestModel) {
  def apply(value: String): TestModel = TestModel(None, value)
}