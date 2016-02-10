package io.farragoLabs.crudRoute

import scala.concurrent.Future

trait CrudDriver[R] {
  def createModel(model: R): Future[Int]
  def readModelById(id: Int): Future[Option[R]]
  def listModelIds: Future[Seq[Int]]
  def updateModel(model: R): Future[Boolean]
  def deleteModel(id: Int): Future[Boolean]
}
