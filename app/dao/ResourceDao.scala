package dao

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.{Resource, ResourceDetail}
import vo.ResourceVO

import scala.concurrent.{ExecutionContext, Future}

class ResourceDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._


  val resourceList: TableQuery[Resources] = TableQuery[Resources]
  val resourceDetailList: TableQuery[ResourcesDetail] = TableQuery[ResourcesDetail]

  class Resources(tag: Tag) extends Table[Resource](tag, "MT_RESOURCE") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def resourceKey = column[String]("RESOURCE_KEY")

    def resourceName = column[String]("RESOURCE_NAME")

    def * = (id, resourceKey, resourceName) <>(Resource.tupled, Resource.unapply)
  }

  class ResourcesDetail(tag: Tag) extends Table[ResourceDetail](tag, "MT_RESOURCE_DETAIL") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def resourceText = column[String]("RESOURCE_TEXT")

    def resourceLocale = column[String]("RESOURCE_LOCALE")

    def resourceKey = column[Long]("RESOURCE_KEY")

    def resoureFk = foreignKey("RESOURCE_FK", resourceKey, resourceList)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (id, resourceText, resourceLocale, resourceKey) <>(ResourceDetail.tupled, ResourceDetail.unapply)
  }

  def list: Future[List[Resource]] = {

    val query = resourceList.to[List].result
    //join 문 실행 할건데 그다음 vo 가 필요 할 듯 한데????
    db.run(query)
  }

  def joinList  = {
    val query = for {
      (rd, r) <- resourceDetailList join resourceList on (_.resourceKey === _.id)
    } yield (r.resourceKey, r.resourceName, rd.resourceLocale, rd.resourceText)

    //val  ss = db.run(query.result)
    //for {
    //      list = db.run(query.result).map{ rows => rows.collect {
    //      case (String, String, String, String) => ResourceVO(_,_,_,_)
    //      }
    //
    // }
    //}yield ResourceVO()
    val ss = db.run(query.result)
    //현재 ss 에는 List 데이터가 존재 Seq[String, String, String, String]

    for (s <- ss) {
      for (a <- s) {
        var t : ResourceVO =  ResourceVO(a._1, a._2, a._3, a._4)
        var temp = "t"
      }
    }
  }
}
