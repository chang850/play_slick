package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi
import utils.MyEnv

import scala.concurrent.Future

/**
  * The basic application controller.
  *
  * @param messagesApi            The Play messages API.
  * @param socialProviderRegistry The social provider registry.
  */
class ApplicationController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[MyEnv[User]],socialProviderRegistry: SocialProviderRegistry)
  extends WebController {

  /**
    * Handles the index action.
    *
    * @return The result to display.
    */
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
      case None => Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
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
