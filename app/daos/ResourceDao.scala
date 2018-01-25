package daos

import javax.inject.Inject

import forms.ResourceAddForm
import models.{ModelTable, Page}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import vo.{ResourceListVO, ResourceVO}

import scala.concurrent.{ExecutionContext, Future}

class ResourceDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ModelTable {

  import profile.api._

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

  //  조회
  //  def list: Future[List[Resource]] = {
  //    val query = resourceList.to[List].result
  //    db.run(query)
  //  }

  //  def insert(resourceDetail: ResourceDetail): Future[Unit] = {
  //    db.run(resourceDetailList += resourceDetail).map(_ => ())
  //  }

  //One To Many 저장 완료
  def saveResourceForm(resourceForm: ResourceAddForm.Data): Future[Seq[Long]] = {
    val resourceData = resourceForm.resource
    val resourceDataDetailList = resourceForm.resourceDetailList
    val interaction = for {
      resourceId <- resourceList returning resourceList.map(_.id) += Resource(resourceData.id, resourceData.resourceKey, resourceData.resourceName)
      id <- DBIO.sequence(resourceDataDetailList.map(u => insert(ResourceDetail(u.id, u.resourceText, u.resourceLocale, resourceId))))
      //          resourceDetailData <- resourceDataDetailList
      //          id <- insert(ResourceDetail(resourceDetailData.id, resourceDetailData.resourceText, resourceDetailData.resourceLocale, resourceId))
    } yield id

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
  //  def joinList : Future[List[ResourceVO]] = {
  //    val query = for {
  //      (rd, r) <- resourceList joinLeft resourceDetailList on (_.id === _.resourceKey)
  //    } yield (rd.resourceKey, rd.resourceName, r.resourceLocale, r.resourceText)
  //    db.run(query.to[List].result).map(u => u.map(a => ResourceVO(a._1, a._2, a._3, a._4)))
  //  }

  //ResourceListVO
  def joinList: Future[List[ResourceVO]] = {
    val query = for {
      (rd, r) <- resourceList joinLeft resourceDetailList on (_.id === _.resourceKey)
    } yield (rd.resourceKey, rd.resourceName, r.map(_.resourceLocale), r.map(_.resourceText))
    db.run(query.to[List].result).map(u => u.map(a => a match {
      case (a, b, c, d) => ResourceVO(a, b, c, d)
    }))
  }

  def findById(id: Long): Future[Option[Resource]] = {
    db.run(resourceList.filter(_.id === id).result.headOption)
  }

  def findByResourceId(resourceId: Long): Future[List[ResourceDetail]] = {
    db.run(resourceDetailList.filter(_.resourceKey === resourceId).to[List].result)
  }


  def list(searchVO: ResourceVO.SearchVO): Future[List[ResourceVO]] = {
    val query = for {
      (rd, r) <- resourceList joinLeft resourceDetailList on (_.id === _.resourceKey)
    } yield (rd.resourceKey, rd.resourceName, r.map(_.resourceLocale), r.map(_.resourceText))
    db.run(query.to[List].result).map(u => u.map(a => ResourceVO(a._1, a._2, a._3, a._4)))
  }

  //Future Type = > Page[ResourceListVO] 를 넘기는 방식
  def pageList(searchVO: ResourceVO.SearchVO): Future[Page[ResourceListVO]] = {
    val query = for {
      (rd, r) <- resourceList joinLeft resourceDetailList on (_.id === _.resourceKey)
    } yield (rd.resourceKey, rd.resourceName, r.map(_.resourceLocale), r.map(_.resourceText))

    for {
      result <- db.run(query.to[List].result)
    } yield Page(result.map(r => ResourceListVO(r._1, r._2, r._3, r._4)), 1, 2, 3)
  }


  //  def userCompanylist: Future[Page[UserListVO]] = {
  //    //JOIN 문 생성
  //    val query = for {
  //      (u, c) <- Users join Companys on (_.company === _.id)
  //    } yield (c.name, u.username, u.email)
  //
  //
  //    //db.run(query.to[List].result).map(r => r.map(a => UserListVO(a._1, a._2, a._3)))
  //    for {
  //      result <- db.run(query.to[List].result)
  //    } yield Page(result.map(r => UserListVO(r._1, r._2, r._3)), 1, 2, 3)
  //
  //    //    for {
  //    //      list = query.result.map { rows => UserListVO(rows.name, rows.username, rows.email)
  //    //
  //    //        }
  //    //      }
  //    //     result = db.run(list)
  //    //    } yield UserListVO("", "" ,"")
  //    //
  //    //    val result = db.run(query.result)
  //
  //    //여기서 List를 어떻게 할것인가????????
  //    //java code 라면 class 생성 해서 해당 list 를 만들어서 리턴인데.............
  //    //    for(r <-result) {
  //    //      for(a <-r){
  //    //        var t:UserListVO = UserListVO(a._1, a._2, a._3)
  //    //      }
  //    //    }
  //
  //  }
}




