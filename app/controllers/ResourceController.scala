package controllers

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}

class ResourceController @Inject()(val messagesApi: MessagesApi) extends BaseController with I18nSupport {


}
