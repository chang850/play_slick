package controllers

import base.Secured
import play.api.i18n.I18nSupport
import play.api.mvc.Controller

abstract class BaseController extends Controller with Secured with I18nSupport {

}
