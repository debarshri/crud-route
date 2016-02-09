package io.farragoLabs.slickSprayRoute

import shapeless.{::, HNil}
import spray.http.StatusCodes._

import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._

import spray.routing.Directives._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.language.experimental.macros
import scala.util.{Try, Failure, Success}

/**
  * Created by fydio on 6/2/15.
  */


class CrudRoute[R: RootJsonFormat](val driver: CrudDriver[R]) extends Route{

  override def apply(ctx: RequestContext): Unit = {
    (createRoute
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
      case r :: HNil => provide(driver.createModel(r))
    }
  }

  def readDirective: Directive1[Future[Option[R]]] = {
    get hflatMap {
      case _ => path(IntNumber)
    } hflatMap {
      case requestedId :: HNil => {
        provide(driver.readModelById(requestedId))
      }
    }
  }

  def listDirective: Directive1[Future[Seq[Int]]] = {
    get hflatMap {
      case _ => provide(driver.listModelIds)
    }
  }

  def updateDirective: Directive1[Future[Boolean]] = {
    post hflatMap {
      case _ => entity(as[R])
    } hflatMap {
      case r :: HNil => provide(driver.updateModel(r))
    }
  }

  def deleteDirective: Directive1[Future[Boolean]] = {
    delete hflatMap {
      case _ => path(IntNumber)
    } hflatMap {
      case id :: HNil => provide(driver.deleteModel(id))
    }
  }
}
