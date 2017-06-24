package controllers

import javax.inject.Inject

import dao.ResourceDao
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action

class ResourceController @Inject()(resourceDao: ResourceDao)(val messagesApi: MessagesApi) extends BaseController with I18nSupport {

  def list  = Action.async{ implicit rs =>
    resourceDao.joinList
    resourceDao.list.map(resource => Ok(views.html.resource.list(resource)))
  }
}
