package dao

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.Resource

import scala.concurrent.ExecutionContext

class ResourceDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class Resources(tag: Tag) extends Table[Resource](tag, "RESOURCE") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def resourceType = column[String]("RESOURCE_TYPE")

    def product = column[String]("PRODUCT")

    def codeMajor = column[String]("CODE_MAJOR")

    def codeMinor = column[String]("CODE_MINOR")

    def resourceId = column[String]("RESOURCE_ID")

    def description = column[String]("DESCRIPTION")

    def * = (id, resourceType, product, codeMajor, codeMinor, resourceId, description) <>(Resource.tupled, Resource.unapply)
  }

}
