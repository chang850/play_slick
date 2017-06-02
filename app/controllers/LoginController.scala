package controllers

import play.api.mvc.{Action, Controller, Security}
import play.api.data.Forms._
import play.api.data._

class LoginController extends Controller {
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying("Invalid email of password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def check(username: String, password: String) = {
    (username == "thomas@home" && password == "1234")
  }

  def login = Action { implicit rs =>
    Ok(views.html.login(loginForm))
  }

  def authenticate = Action { implicit rs =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.UserController.list).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.LoginController.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}
