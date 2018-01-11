package dao

import com.mohiva.play.silhouette.api.LoginInfo
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait DBTableDefinitions extends HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  case class DBUser (
    userID: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String]
  )

  class Users(tag: Tag) extends Table[DBUser](tag, "USER") {
    def id = column[String]("USERID", O.PrimaryKey)
    def firstName = column[Option[String]]("FIRSTNAME")
    def lastName = column[Option[String]]("LASTNAME")
    def fullName = column[Option[String]]("FULLNAME")
    def email = column[Option[String]]("EMAIL")
    def avatarURL = column[Option[String]]("AVATARURL")
    def * = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo (
    id: Option[Long],
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "LOGININFO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("PROVIDERID")
    def providerKey = column[String]("PROVIDERKEY")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo (
    userID: String,
    loginInfoId: Long
  )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "USERLOGININFO") {
    def userID = column[String]("USERID")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo (
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "PASSWORDINFO") {
    def hasher = column[String]("HASHER")
    def password = column[String]("PASSWORD")
    def salt = column[Option[String]]("SALT")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info (
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long
  )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "OAUTH1INFO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def token = column[String]("TOKEN")
    def secret = column[String]("SECRET")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info (
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long
  )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "OAUTH2INFO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("ACCESSTOKEN")
    def tokenType = column[Option[String]]("TOKENTYPE")
    def expiresIn = column[Option[Int]]("EXPIRESIN")
    def refreshToken = column[Option[String]]("REFRESHTOKEN")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }
  
  case class DBOpenIDInfo (
    id: String,
    loginInfoId: Long
  )
  
  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "OPENIDINFO") {
    def id = column[String]("ID", O.PrimaryKey)
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (id, loginInfoId) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }
  
  case class DBOpenIDAttribute (
    id: String,
    key: String,
    value: String
  )
  
  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "OPENIDATTRIBUTES") {
    def id = column[String]("ID")
    def key = column[String]("KEYTYPE")
    def value = column[String]("VALUETYPE")
    def * = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickOpenIDInfos = TableQuery[OpenIDInfos]
  val slickOpenIDAttributes = TableQuery[OpenIDAttributes]
  
  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) = 
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
