package utils

import javax.inject.{Inject, Provider, Singleton}

import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import controllers.routes
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}

import scala.concurrent.Future

/**
  * A secured error handler.
  */

@Singleton
class ErrorHandler @Inject()(
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router],
                              val messagesApi: MessagesApi
                            ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with SecuredErrorHandler with UnsecuredErrorHandler  with I18nSupport {

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request  The request header.
    * @return The result to send to the client.
    */
    override def onNotAuthenticated(implicit request: RequestHeader) = {
      Future.successful(Redirect(routes.ApplicationController.signIn()))
    }


  //  override def onNotAuthenticated(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
  //    Some(Future.successful(Redirect(routes.ApplicationController.signIn())))
  //  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request  The request header.
    * @return The result to send to the client.
    */
    override def onNotAuthorized(implicit request: RequestHeader) = {
      Future.successful(Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("access.denied")))
    }
  //  override def onNotAuthorized(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
  //    Some(Future.successful(Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("access.denied")(messages))))
  //  }



}
