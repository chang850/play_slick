package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import forms._
import models.User
import play.api.i18n.MessagesApi
import utils.MyEnv

import scala.concurrent.Future

class ApplicationController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[MyEnv[User]]) extends WebController {

  /**
    * Handles the index action.
    *
    * @return The result to display.
    */
  //시작 점 home
  def index = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.home(request.identity)))
  }

  /**
    * Handles the Sign In action.
    *
    * @return The result to display.
    */
  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.signIn(SignInForm.form)))
    }
  }

  /**
    * Handles the Sign Up action.
    *
    * @return The result to display.
    */
  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }

  /**
    * Handles the Sign Out action.
    *
    * @return The result to display.
    */
  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    env.eventBus.publish(LogoutEvent(request.identity, request))
    env.authenticatorService.discard(request.authenticator, result)
  }
}
