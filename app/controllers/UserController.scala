package controllers

import javax.inject.Inject

import models.UserDao
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
      "password" -> nonEmptyText,
      "company" -> ignored(0L)
    )(models.User.apply)(models.User.unapply))

  //Form
  def form = withAuth { username => implicit rs =>
    Ok(views.html.user.userForm(userform))
  }

  //File Upload 구현
  def uploadFile = Action(parse.multipartFormData) { rs =>
    rs.body.file("picture").map{ picture =>
      import java.io.File
      val filename = picture.filename
      picture.ref.moveTo(new File(s"/tmp/picture/$filename"))
      Ok("파일 업로드 완료")
    }.getOrElse{
      Redirect(routes.UserController.list()).flashing(
        "error" ->"파일이 없습니다."
      )
    }
  }


  /* User Join On Company --> UserListVO */
  def list = Action.async { implicit rs =>
    userDAO.list.map(users => Ok(views.html.user.list(users)))
  }

  //Page List
  def pageList(page: Int) = Action.async { implicit rs =>
    val userList = userDAO.page(page = page)
    userList.map(users => Ok(views.html.user.page(users, 1, "%")))
  }

  //삭제
  def delete(id: Long) = Action.async { implicit rs =>
    userDAO.delete(id).map(users => Ok)
  }

  //저장
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

  //상세보기
  def view(id: Long) = Action.async { implicit rs =>
    for {
      Some(user) <- userDAO.view(id)
    } yield Ok(views.html.user.view(user))
  }
}
