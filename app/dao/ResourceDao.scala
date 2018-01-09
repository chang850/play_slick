package dao

import javax.inject.Inject

import FormVO.ResourceForm
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

  //Resource
  class Resources(tag: Tag) extends Table[Resource](tag, "MT_RESOURCE") {
    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    def resourceKey = column[String]("RESOURCE_KEY")
    def resourceName = column[String]("RESOURCE_NAME")
    def * = (id, resourceKey, resourceName) <> (Resource.tupled, Resource.unapply)
  }

  //ResourceDetail List
  class ResourcesDetail(tag: Tag) extends Table[ResourceDetail](tag, "MT_RESOURCE_DETAIL") {
    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    def resourceText = column[String]("RESOURCE_TEXT")
    def resourceLocale = column[String]("RESOURCE_LOCALE")
    def resourceKey = column[Long]("RESOURCE_KEY")
    def resoureFk = foreignKey("RESOURCE_FK", resourceKey, resourceList)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
    def * = (id, resourceText, resourceLocale, resourceKey) <> (ResourceDetail.tupled, ResourceDetail.unapply)
  }

  //조회
  def list: Future[List[Resource]] = {
    val query = resourceList.to[List].result
    db.run(query)
  }

  //delete query
  def delete(id: Long): Future[Int] = {

    val query = resourceList.filter(_.id === id)
    val interaction = for {
      resources <- query.result
      _ <- DBIO.sequence(resources.map(r => resourceDetailList.filter(_.resourceKey === r.id).delete))
      resourcesDeleted <- query.delete
    } yield resourcesDeleted

    db.run(interaction.transactionally)
  }

  //저장
  def save(resource: Resource): Future[Unit] = {
    db.run(resourceList += resource).map(_ => ())
  }

  //Id 를 통해 resource 를 찾는 로직
  def resourceFindById(id: Long): Future[Option[Resource]] = {
    db.run(resourceList.filter(_.id === id).result.headOption)
  }

  def insert(resourceDetail: ResourceDetail): DBIO[Long] = {
    resourceDetailList returning resourceDetailList.map(_.id) += resourceDetail
  }

//  def insert(resourceDetail: ResourceDetail): Future[Unit] = {
//    db.run(resourceDetailList += resourceDetail).map(_ => ())
//  }

  //One To Many 저장 완료
  def saveResourceForm(resourceForm: ResourceForm): Future[Seq[Long]] = {

    val resourceData = resourceForm.resource
    val resourceDataDetailList = resourceForm.resourceDetailList
    val interaction = for {
          resourceId <- resourceList returning resourceList.map(_.id) += resourceData
          id <-DBIO.sequence(resourceDataDetailList.map(u=>insert(ResourceDetail(u.id,u.resourceText, u.resourceLocale, resourceId))))
//          resourceDetailData <- resourceDataDetailList
//          id <- insert(ResourceDetail(resourceDetailData.id, resourceDetailData.resourceText, resourceDetailData.resourceLocale, resourceId))
     }yield id

    db.run(interaction.transactionally)
    //    val resourceData = Resource(1, "changhee", "mp")
    //    val resourceDataDetailList : Seq[ResourceDetail] = Seq(ResourceDetail(1,"changhee", "mp9709", 1))
//    resourceDataDetailList match {
//      case Nil => save(resourceData)
//      case _=> val interaction = for {
//        resourceId <- resourceList returning resourceList.map(_.id) += resourceData
//        resourceDetailData <- resourceDataDetailList
//        id <- insert(ResourceDetail(resourceDetailData.id, resourceDetailData.resourceText, resourceDetailData.resourceLocale, resourceDetailData.id))
//      }yield id
//
//      db.run(interaction.transactionally)
//    }
//    val interaction = for {
//      resourceDetailData <- resourceDataDetailList
//      id <- insert(ResourceDetail(resourceDetailData.id, resourceDetailData.resourceText, resourceDetailData.resourceLocale, resourceId))
//    }yield id
  }

  //조인 ResourceVO 완료
  def joinList = {
    val query = for {
      (rd, r) <- resourceDetailList join resourceList on (_.resourceKey === _.id)
    } yield (r.resourceKey, r.resourceName, rd.resourceLocale, rd.resourceText)
    db.run(query.result).map(u => u.map(a => ResourceVO(a._1, a._2, a._3, a._4)))
  }
}




