package io.farragoLabs.crudRoute

import slick.driver.JdbcDriver
import spray.json.RootJsonFormat

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SlickCrudDriverProvider[A <: JdbcDriver](val driver: A) {

  import driver.api._

  def apply[R: RootJsonFormat, T <: Table[R]]
  (rowIdProjection: T => Rep[Int], modelIdProjection: R => Int)
  (implicit db: Database, table: TableQuery[T { type TableElementType = R}]): SlickCrudDriver[R, T] = {
    new SlickCrudDriver[R, T](rowIdProjection, modelIdProjection, db, table): SlickCrudDriver[R, T]
  }

  class SlickCrudDriver[R: RootJsonFormat, T <: Table[R]]
  (val rowId: T => Rep[Int], val modelId: R => Int, db: Database, table: TableQuery[T { type TableElementType = R}]) extends CrudDriver[R] {

    def createModel(model: R): Future[Int] = {
      val q = table.returning(table.map(rowId)) += model

      db.run(q)
    }

    def readModelById(id: Int): Future[Option[R]] = {
      val q = table
        .filter(row => rowId(row) === id)

      db.run(q.result.headOption)
    }

    def listModelIds: Future[Seq[Int]] = {
      val q = table
        .map(row => rowId(row))
        .result

      db.run(q)
    }

    def updateModel(model: R): Future[Boolean] = {
      val q = table
        .filter(row => rowId(row) === modelId(model))
        .update(model)

      db.run(q).map(_ == 1)
    }

    def deleteModel(id: Int): Future[Boolean] = {
      val q = table
        .filter(row => rowId(row) === id).delete

      db.run(q).map(_ == 1)
    }
  }
}
