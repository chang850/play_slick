package controllers

import javax.inject.Inject

import dao.UserDao
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action

class UserController @Inject()(userDAO: UserDao, val messagesApi: MessagesApi) extends BaseController with I18nSupport {

  val Home = Redirect(routes.UserController.list)

  val userform = Form(
    mapping(
      "id" -> ignored(0L),
      "username" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(models.User.apply)(models.User.unapply))

  def form = withAuth { username => implicit rs =>
    Ok(views.html.user.userForm(userform))
  }

  def list = Action.async { implicit rs =>
    userDAO.list.map(users => Ok(views.html.user.list(users)))
  }

  def pageList(page: Int) = Action.async { implicit rs =>
    val userList = userDAO.page(page = page)
    userList.map(users => Ok(views.html.user.page(users, 1, "%")))
  }

  def delete(id: Long) = Action.async { implicit rs =>
    userDAO.delete(id).map(users => Ok)
  }

  def save = Action.async { implicit rs =>
    userform.bindFromRequest.fold(
      formWithErrors => userDAO.list.map(users => BadRequest(views.html.user.userForm(formWithErrors))),
      user => {
        for {
          _ <- userDAO.save(user)
        } yield Home.flashing("success" -> "Computer %s has been created".format(user.username))
      }
    )
  }

  def view(id: Long) = Action.async { implicit rs =>
    for {
      Some(user) <- userDAO.view(id)
    } yield Ok(views.html.user.view(user))
  }
}
