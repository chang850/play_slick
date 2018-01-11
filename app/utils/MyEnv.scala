package utils

import com.mohiva.play.silhouette.api.{Env, Identity}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator


//trait 생성
//type 이 무엇인지
trait MyEnv[Id <: Identity] extends Env{
  type I = Id
  type A = CookieAuthenticator
}
