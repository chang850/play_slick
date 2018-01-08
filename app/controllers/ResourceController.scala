package controllers

import javax.inject.Inject

import FormVO.ResourceForm
import play.api.data.Forms._
import dao.ResourceDao
import models.{Resource, ResourceData, ResourceDetail}
import play.api.data.Form
import play.api.data.Forms.{ignored, mapping}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action


class ResourceController @Inject()(resourceDao: ResourceDao)(val messagesApi: MessagesApi) extends BaseController with I18nSupport {

  val Home = Redirect(routes.UserController.list)

  //Form Create
//  val resourceForm = Form(
//    mapping(
//      "id" -> ignored(0L),
//      "resourceKey" -> nonEmptyText,
//      "resourceName" -> nonEmptyText
//    )(models.Resource.apply)(models.Resource.unapply))

//  val resourceDataForm : Form[ResourceData] = Form(
//    mapping(
//      "id" -> ignored(0L),
//      "rKey" -> nonEmptyText,
//      "rName" -> nonEmptyText,
//      "resourceDetailList" -> seq[Resource](
//        mapping(
//          "id" -> ignored(0L),
//          "resourceKey" -> nonEmptyText,
//          "resourceName" -> nonEmptyText
//        )(models.Resource.apply)(models.Resource.unapply))
//    )(models.ResourceData.apply)(models.ResourceData.unapply))
  val resourceForm : Form[ResourceForm] = Form(
        mapping(
          "resource" -> mapping(
                  "id" -> ignored(0L),
                  "resourceKey" -> nonEmptyText,
                  "resourceName" -> nonEmptyText
           )(models.Resource.apply)(models.Resource.unapply),
          "resourceDetailList" -> seq[ResourceDetail](
                mapping(
                  "id" -> ignored(0L),
                  "resourceText" -> nonEmptyText,
                  "resourceLocale" ->nonEmptyText,
                  "resourceKey" -> ignored(0L)
             )(models.ResourceDetail.apply)(models.ResourceDetail.unapply))
        )(FormVO.ResourceForm.apply)(FormVO.ResourceForm.unapply))

  def list = Action.async { implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }

  def create = Action { implicit rs =>
      Ok(views.html.resource.add(resourceForm))
  }


  //한방에 저장 하려면 해당 vo 를 조합 하는 형태로 만든다.
  //저장
  def save= Action.async { implicit rs =>
      resourceForm.bindFromRequest.fold(
            formWithErrors => resourceDao.list.map(resoures => BadRequest(views.html.resource.add(formWithErrors))),
            resourceData => {
            for {
              _ <- resourceDao.saveResourceForm(resourceData)
            } yield Home.flashing("success" -> "Computer %s has been created".format("chnag"))
          }
     )
  }

  //갱신
  def update(name: String) = Action.async { implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }

  //삭제
  def delete(name: String) = Action.async { implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }

  //상세보기
  def view(name: String) = Action.async { implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }
}
