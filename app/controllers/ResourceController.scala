package controllers

import javax.inject.Inject

import FormVO.ResourceForm
import dao.ResourceDao
import models.ResourceDetail
import play.api.data.Form
import play.api.data.Forms.{ignored, mapping, _}
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action


class ResourceController @Inject()(resourceDao: ResourceDao)(val messagesApi: MessagesApi) extends BaseController {

  val Home = Redirect(routes.UserController.list)

  //1.pagination
  //2.search
  //3.authentication
  //4.Flyway
  //5.cache
  //6.schelduled

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

  //작업 진행중
  //search Form 작업
  //search Form 을 만들고 List 와 함께 던진다.
  //search Form 과 함께 받은 후 list 페이지에서 Data를 꺼낸뒤 바인딩 한 후 던지는 식으로 진행
  //List 성공
  //현재 List 인데 --> search 생성하고 + list 거기서 가져온뒤 뿌린다.
  def list = Action.async { implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }

  //Form Data 생성
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


  //삭제 - 완료
  def delete(id: Long) = Action.async { implicit rs =>
    resourceDao.delete(id).map(num => Ok(s"$num projects deleted"))
  }

  //작업 진행중
  //갱신
  def update(name: String) = Action.async { implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }

  //작업 진행중
  //상세보기 ===> Resource + ResourceDetailList 로 해야한다.
  def view(id: Long) = Action.async { implicit rs =>
    //두단계로 뿌릴 것인가 ? vo 를 만들 것인가 선택
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }
}
