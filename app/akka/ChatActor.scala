package akka

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import play.api.libs.json.Json

case class Join(username: String)
case class Leave(username: String)
case class Talk(usrname: String, message: String)

class ChatActor(actorRef: ActorRef) extends Actor {
  override def receive: Receive = {
    case str: String => {
      val json = Json.parse(str)
      (json \ "type").as[String] match {
        case "join" => self ! Join((json \ "username").as[String])
        case "leave" => self ! Leave((json \ "username").as[String])
        case "talk" => self ! Talk((json \ "username").as[String], (json \ "msg").as[String])
      }
    }
    case Join(username) => {
      ChatActor.users = (username, actorRef) :: ChatActor.users
      actorRef ! ("Chatting Room Entrance")
      broadcast(username +"is Chatting Join.")
    }
    case Leave(username) => {
      ChatActor.users = ChatActor.users.filter(u => u._1 != username)
      broadcast(username + "is Exit")
      actorRef ! ("Chatting Room Exit")
      actorRef ! PoisonPill
    }
    case Talk(username, msg) => {
      broadcast(username + ":" + msg)
    }
  }

  def broadcast(msg: String) = {
    ChatActor.users.foreach(u => u._2 ! msg)
  }
}

object ChatActor {
  var users = List[(String, ActorRef)]()
  def props(actor: ActorRef) = Props(new ChatActor(actor))
}


