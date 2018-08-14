package model

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import repository.Repo

import scala.concurrent.ExecutionContextExecutor

object UserHandler {
  def props(db: Repo) = Props(new UserHandler(db))

  case class User(username: String, details: String)
  case class Register(username: String, password: String)
  case class Update(username: String, details: String)
  case class GetUser(username: String)
  case class DeleteUser(username: String)
  case class UserNotFound(username: String)
  case class UserDeleted(username: String)
}

class UserHandler(db: Repo) extends Actor with ActorLogging {
  import UserHandler._
  implicit val ec: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {
    case Register(id, pwd) =>
      db.upsert(id, pwd) pipeTo sender()

    case Update(id, details) =>
      db.upsert(id, details) pipeTo sender()

    case GetUser(username) =>
      val requestor = sender()
      db.get(username).foreach {
        case Some(details) => requestor ! User(username, details)
        case None => requestor ! UserNotFound
      }

    case DeleteUser(username) =>
      val requestor = sender()
      db.del(username).foreach {
        case effectedRows if effectedRows > 0 =>
          requestor ! UserDeleted(username)
        case _ => requestor ! UserNotFound(username)
      }
  }



}
