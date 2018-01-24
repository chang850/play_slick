package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import daos.ResourceDao
import forms.{ResourceAddForm, ResourceSearchForm}
import models.User
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, AnyContent}
import utils.MyEnv
import vo.ResourceVO


class ResourceController @Inject()(resourceDao: ResourceDao)(val messagesApi: MessagesApi , val silhouette: Silhouette[MyEnv[User]])
  extends WebController with I18nSupport{

  val Home = Redirect(routes.ResourceController.list)

//  val userform = Form(
//    mapping(
//      "id" -> ignored(0L),
//      "username" -> nonEmptyText,
//      "email" -> nonEmptyText,
//      "password" -> nonEmptyText,
//      "company" -> ignored(0L)
//    )(BaseEntity.User.apply)(BaseEntity.User.unapply))
//
//  //사용자 추가 폼을 내려주는데... //popup call 할때 정보 내려주는 식으로 하면 될듯
//  def form = withAuth { username => implicit rs =>
//    Ok(views.html.user.userForm(userform))
//  }
//
//  //한글 TEST
//  //한글 test 2
//  //한글 test 3
//  def uploadFile = Action(parse.multipartFormData) { rs =>
//    rs.body.file("picture").map{ picture =>
//      import java.io.File
//      val filename = picture.filename
//      picture.ref.moveTo(new File(s"/tmp/picture/$filename"))
//      Ok("파일 업로드 완료")
//    }.getOrElse{
//      Redirect(routes.UserController.list()).flashing(
//        "error" ->"파일이 없습니다."
//      )
//    }
//  }
//
//
//  def list = Action.async { implicit rs =>
//    userDao.list.map(users => Ok(views.html.user.list(users)))
//  }
//
//  //Page List
//  def pageList(page: Int) = Action.async { implicit rs =>
//    userDao.page(page).map(users => Ok(views.html.user.page(users, 1, "%")))
//  }
//
//  //삭제
//  def delete(id: Long) = Action.async { implicit rs =>
//    userDao.delete(id).map(users => Ok)
//  }
//
//  //저장
//  def save = Action.async { implicit rs =>
//    userform.bindFromRequest.fold(
//      formWithErrors => userDao.list.map(users => BadRequest(views.html.user.userForm(formWithErrors))),
//      user => {
//        for {
//          _ <- userDao.save(user)
//        } yield Home.flashing("success" -> "Computer %s has been created".format(user.username))
//      }
//    )
//  }
//
//  //상세보기
//  def view(id: Long) = Action.async { implicit rs =>
//    for {
//      Some(user) <- userDao.view(id)
//    } yield Ok(views.html.user.view(user))
//  }

  //Secure social

  //1.pagination
  //2.search
  //3.authentication -> pagination 은 일단 무시
  //4.Flyway
  //5.cache
  //6.schelduled


  //Form Create
  //  val resourceForm = Form(
  //    mapping(
  //      "id" -> ignored(0L),
  //      "resourceKey" -> nonEmptyText,
  //      "resourceName" -> nonEmptyText
  //    )(BaseEntity.Resource.apply)(BaseEntity.Resource.unapply))

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
  //        )(BaseEntity.Resource.apply)(BaseEntity.Resource.unapply))
  //    )(BaseEntity.ResourceData.apply)(BaseEntity.ResourceData.unapply))
//  val resourceForm: Form[ResourceForm] = Form(
//    mapping(
//      "resource" -> mapping(
//        "id" -> ignored(0L),
//        "resourceKey" -> nonEmptyText,
//        "resourceName" -> nonEmptyText
//      )(BaseEntity.Resource.apply)(BaseEntity.Resource.unapply),
//      "resourceDetailList" -> seq[ResourceDetail](
//        mapping(
//          "id" -> ignored(0L),
//          "resourceText" -> nonEmptyText,
//          "resourceLocale" -> nonEmptyText,
//          "resourceKey" -> ignored(0L)
//        )(BaseEntity.ResourceDetail.apply)(BaseEntity.ResourceDetail.unapply))
//    )(FormVO.ResourceForm.apply)(FormVO.ResourceForm.unapply))

  //search Form 작업 완료
  //1.select box
  //2.radio box
  //3.check boxk
  //4.text
//  val resourceSearchForm: Form[ResourceVO.SearchVO] = Form(
//    mapping(
//      "keywordString" -> text,
//      "optionSelect" -> text,
//      "radioInput" -> text
//    )(vo.ResourceVO.SearchVO.apply)(vo.ResourceVO.SearchVO.unapply)
//  )
  //작업 진행중
  //search Form 작업
  //search Form 을 만들고 List 와 함께 던진다.
  //search Form 과 함께 받은 후 list 페이지에서 Data를 꺼낸뒤 바인딩 한 후 던지는 식으로 진행
  //List 성공
  //현재 List 인데 --> search 생성하고 + list 거기서 가져온뒤 뿌린다.

  //여기서 Form 과 함께 던질것인데

  //Form 만들고
  //여기가 List 라면
  //Search 바인딩은 어디서 해야 하나
  //list + searchForm => searchForm
  //page 를 여기서 탈것인데.............

  def list: Action[AnyContent] = SecuredAction.async  { implicit rs =>
    ResourceSearchForm.form.bindFromRequest.fold(
      formWithErrors => resourceDao.joinList.map(resoures => Ok(views.html.resource.list(resoures, ResourceSearchForm.form))),
      resourceData => {
        for {
          resource <- resourceDao.list(resourceData)
        } yield Ok(views.html.resource.list(resource, ResourceSearchForm.form))
      }
    )
    //resourceDao.joinList.map(resource=> Ok(views.html.resource.joinlist(resource, resourceSearchForm)))
  }

  //Form Data 생성
  def create = Action { implicit rs =>
    Ok(views.html.resource.add(ResourceAddForm.form))
  }

  //한방에 저장 하려면 해당 vo 를 조합 하는 형태로 만든다.
  //저장
  def save: Action[AnyContent] = Action.async { implicit rs =>
    ResourceAddForm.form.bindFromRequest.fold(
      formWithErrors => resourceDao.joinList.map(resoures => BadRequest(views.html.resource.add(formWithErrors))),
      resourceData => {
        for {
          _ <- resourceDao.saveResourceForm(resourceData)
        } yield Home.flashing("success" -> "Computer %s has been created".format("chnag"))
      }
    )
  }



  //삭제 - 완료
  def delete(id: Long): Action[AnyContent] = Action.async { implicit rs =>
    resourceDao.delete(id).map(num => Ok(s"$num projects deleted"))
  }

  //작업 진행중
  //갱신
//  def update(name: String) = Action.async { implicit rs =>
//    resourceDao.joinList
//    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
//  }

  //Model 객체 에 집어 넣을수 가 없음
  //작업 진행중
  //상세보기 ===> Resource + ResourceDetailList 로 해야한다.
  //def view(id: Long) = Action.async { implicit rs =>
  // for {
  //  Some(resource) <- resourceDao.findById(id)
    //resourceDetail <- resourceDao.findByResourceId(id)
  // } yield Ok(views.html.resource.view(resource,  resourceDetail))
//  }
}
