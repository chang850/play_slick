package controllers

import javax.inject.Inject

import akka.ChatActor
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, Controller, WebSocket}


class AkkaController @Inject() extends Controller {
  implicit val system = ActorSystem("MyActorSystem")
  implicit val materializer = ActorMaterializer()

  def chatStart = Action { implicit rs =>
    Ok(views.html.akka(rs))
  }

  def chat = WebSocket.accept[String, String] { rs =>
    ActorFlow.actorRef(actor => ChatActor.props(actor))
  }
}
