package farragoLabs.io.slickSprayRoute

import akka.util.Timeout
import shapeless.{HNil, ::}

import scala.util.{Success, Failure}
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import spray.routing.PathMatchers.IntNumber
import spray.routing.Directives._
import spray.http.StatusCodes._

import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.routing.{Directive1, RequestContext, Route}

import slick.driver.H2Driver.api._

/**
  * Created by fydio on 6/2/15.
  */
class CrudRoute[T <: Table[R], R: RootJsonFormat](
                                                   val rowId: T => Rep[Int],
                                                   val modelId: R => Int)(
                                                   implicit db: Database,
                                                   table: TableQuery[T { type TableElementType = R}]) extends Route {

  override def apply(ctx: RequestContext): Unit = {
    ( createRoute
      ~ readRoute
      ~ updateRoute
      ~ deleteRoute
      ~ listRoute).apply(ctx)
  }

  def createRoute: Route = {
    createDirective { createModel =>
      onComplete(createModel) {
        case Success(insertId: Int) => complete("" + insertId)
        case Failure(e) => complete(BadRequest, e)
      }
    }
  }

  def readRoute: Route = {
    readDirective { readModel =>
      onComplete(readModel) {
        case Success(Some(model)) => complete(model)
        case Success(None) => complete(NotFound)
        case Failure(e) => complete(InternalServerError, e)
      }
    }
  }

  def listRoute: Route = {
    listDirective { ids =>
      onComplete(ids) {
        case Success(ids: Seq[Int]) => complete(ids)
        case Failure(e) => complete(InternalServerError, e)
      }
    }
  }

  def updateRoute: Route = {
    updateDirective { updateModel =>
      println("Update!!!")
      onComplete(updateModel) {
        case Success(true) => complete(OK)
        case Success(false) => complete(NotFound)
        case Failure(e) => complete(InternalServerError, e)
      }
    }
  }

  def deleteRoute: Route = {
    deleteDirective { deleteModel =>
      onComplete(deleteModel) {
        case Success(true) => complete(OK)
        case Success(false) => complete(NotFound)
        case Failure(e) => complete(InternalServerError, e)
      }
    }
  }

  def createDirective: Directive1[Future[Int]] = {
    put hflatMap {
      case _ => entity(as[R])
    } hflatMap {
      case r::HNil => provide(createModel(r))
    }
  }

  def readDirective: Directive1[Future[Option[R]]] = {
    get hflatMap {
      case _ =>  path(IntNumber)
    } hflatMap {
      case requestedId::HNil => {
        provide(readModelById(requestedId))
      }
    }
  }

  def listDirective: Directive1[Future[Seq[Int]]] = {
    get hflatMap {
      case _ => provide(listModelIds)
    }
  }

  def updateDirective: Directive1[Future[Boolean]] = {
    post hflatMap {
      case _ => entity(as[R])
    } hflatMap {
      case r::HNil => provide(updateModel(r))
    }
  }

  def deleteDirective: Directive1[Future[Boolean]] = {
    delete hflatMap {
      case _ => path(IntNumber)
    } hflatMap {
      case id::HNil => provide(deleteModel(id))
    }
  }

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
