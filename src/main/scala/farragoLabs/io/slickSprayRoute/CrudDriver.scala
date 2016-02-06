package farragoLabs.io.slickSprayRoute

import spray.json.RootJsonFormat
import spray.routing._

import scala.concurrent.Future

/**
  * Created by admin on 2/6/16.
  */
trait CrudDriver[R] {
  def createModel(model: R): Future[Int]
  def readModelById(id: Int): Future[Option[R]]
  def listModelIds: Future[Seq[Int]]
  def updateModel(model: R): Future[Boolean]
  def deleteModel(id: Int): Future[Boolean]
}
