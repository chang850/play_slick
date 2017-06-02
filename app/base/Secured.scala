package base

import play.api.mvc._
import controllers.routes


/**
  * Created by chyun on 2016-12-20.
  */
trait Secured {
  def username(request: RequestHeader) = request.session.get(Security.username)
  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.LoginController.login)
  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) {
      user => Action(request => f(user)(request))
    }
  }
}
