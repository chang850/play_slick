package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

//User Service 를 위한 User
case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String]) extends Identity
