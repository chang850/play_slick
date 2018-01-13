package controllers

import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.{Environment, Identity, Silhouette}
import play.api.i18n.I18nSupport
import play.api.mvc.Controller
import utils.MyEnv

trait AuthController[I <: Identity] extends Controller with I18nSupport {

  def silhouette: Silhouette[MyEnv[I]]
  def env: Environment[MyEnv[I]] = silhouette.env
  def SecuredAction: SecuredActionBuilder[MyEnv[I]] = silhouette.SecuredAction
  def UnsecuredAction: UnsecuredActionBuilder[MyEnv[I]] = silhouette.UnsecuredAction
  def UserAwareAction: UserAwareActionBuilder[MyEnv[I]] = silhouette.UserAwareAction

  implicit def securedRequest2User[A](implicit request: SecuredRequest[MyEnv[I], A]): I = request.identity
  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[MyEnv[I], A]): Option[I] = request.identity
}
