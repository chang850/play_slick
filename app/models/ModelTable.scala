package models

import com.mohiva.play.silhouette.api.LoginInfo
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile



trait ModelTable extends HasDatabaseConfigProvider[JdbcProfile] with ModelEntity{

  import profile.api._

  class Users(tag: Tag) extends Table[DBUser](tag, "USER") {
    def id = column[String]("USERID", O.PrimaryKey)
    def firstName = column[Option[String]]("FIRSTNAME")
    def lastName = column[Option[String]]("LASTNAME")
    def fullName = column[Option[String]]("FULLNAME")
    def email = column[Option[String]]("EMAIL")
    def avatarURL = column[Option[String]]("AVATARURL")
    def * = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "LOGININFO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("PROVIDERID")
    def providerKey = column[String]("PROVIDERKEY")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "USERLOGININFO") {
    def userID = column[String]("USERID")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "PASSWORDINFO") {
    def hasher = column[String]("HASHER")
    def password = column[String]("PASSWORD")
    def salt = column[Option[String]]("SALT")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "OAUTH1INFO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def token = column[String]("TOKEN")
    def secret = column[String]("SECRET")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "OAUTH2INFO") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("ACCESSTOKEN")
    def tokenType = column[Option[String]]("TOKENTYPE")
    def expiresIn = column[Option[Int]]("EXPIRESIN")
    def refreshToken = column[Option[String]]("REFRESHTOKEN")
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "OPENIDINFO") {
    def id = column[String]("ID", O.PrimaryKey)
    def loginInfoId = column[Long]("LOGININFOID")
    def * = (id, loginInfoId) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }

  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "OPENIDATTRIBUTES") {
    def id = column[String]("ID")
    def key = column[String]("KEYTYPE")
    def value = column[String]("VALUETYPE")
    def * = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)
  }


  class Resources(tag: Tag) extends Table[Resource](tag, "MT_RESOURCE") {
    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    def resourceKey = column[String]("RESOURCE_KEY")
    def resourceName = column[String]("RESOURCE_NAME")
    def * = (id, resourceKey, resourceName) <> (Resource.tupled, Resource.unapply)
  }

  class ResourcesDetail(tag: Tag) extends Table[ResourceDetail](tag, "MT_RESOURCE_DETAIL") {
    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    def resourceText = column[String]("RESOURCE_TEXT")
    def resourceLocale = column[String]("RESOURCE_LOCALE")
    def resourceKey = column[Long]("RESOURCE_KEY")
    def resoureFk = foreignKey("RESOURCE_FK", resourceKey, resourceList)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
    def * = (id, resourceText, resourceLocale, resourceKey) <> (ResourceDetail.tupled, ResourceDetail.unapply)
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
  val resourceList = TableQuery[Resources]
  val resourceDetailList =  TableQuery[ResourcesDetail]

  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
