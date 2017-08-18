package models

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import vo.UserListVO

import scala.concurrent.{ExecutionContext, Future}

//model
case class User(id: Long, username: String, email: String, password: String, company: Long)

//mapping
class Users(tag: Tag) extends Table[User](tag, "USER") {
  def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

  def username = column[String]("USERNAME")

  def email = column[String]("EMAIL")

  def password = column[String]("PASSWORD")

  def company = column[Long]("COMPANY")

  def * = (id, username, email, password, company) <>(User.tupled, User.unapply)
}

//쿼리
@Singleton()
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val Companys = TableQuery[Companys]
  val Users = TableQuery[Users]

  def view(id: Long): Future[Option[User]] = {
    db.run(Users.filter(_.id === id).result.headOption)
  }

  def save(user: User): Future[Unit] = {
    db.run(Users += user).map(_ => ())
  }

  //Non-Blocking Future Type
  //  def list: Future[List[User]] = {
  //    db.run(Users.to[List].result)
  //  }

  def delete(id: Long): Future[Int] = {
    val user = Users.filter(_.id === id)
    val action = user.delete
    db.run(action)
  }

  //Non-Blocking Future Type
  def count(filter: String): Future[Int] = {
    db.run(Users.filter { user => user.username.toLowerCase like filter.toLowerCase }.length.result)
  }

  def page(page: Int = 1, pageSize: Int = 1, orderBy: Int = 1, filter: String = "%"): Future[Page[User]] = {
    val offset = pageSize * page
    val query =
      (for {
        user <- Users
      } yield user).drop(offset).take(pageSize)

    for {
      totalRows <- count(filter)
      list = query.result.map { rows => rows.collect {
        case (user) => user
      }
      }
      result <- db.run(list)
    } yield Page(result, page, offset, totalRows)
  }


  def list: Future[List[UserListVO]] = {
    val query = for {
      (u, c) <- Users join Companys on (_.company === _.id)
    } yield (c.name, u.username, u.email)
    db.run(query.to[List].result).map(r => r.map(a => UserListVO(a._1, a._2, a._3)))
  }

  //조인 List
  def userCompanylist: Future[Page[UserListVO]] = {
    //JOIN 문 생성
    val query = for {
      (u, c) <- Users join Companys on (_.company === _.id)
    } yield (c.name, u.username, u.email)


    //db.run(query.to[List].result).map(r => r.map(a => UserListVO(a._1, a._2, a._3)))
    for {
      result <- db.run(query.to[List].result)
    } yield Page(result.map(r => UserListVO(r._1, r._2, r._3)), 1, 2, 3)

    //    for {
    //      list = query.result.map { rows => UserListVO(rows.name, rows.username, rows.email)
    //
    //        }
    //      }
    //     result = db.run(list)
    //    } yield UserListVO("", "" ,"")
    //
    //    val result = db.run(query.result)

    //여기서 List를 어떻게 할것인가????????
    //java code 라면 class 생성 해서 해당 list 를 만들어서 리턴인데.............
    //    for(r <-result) {
    //      for(a <-r){
    //        var t:UserListVO = UserListVO(a._1, a._2, a._3)
    //      }
    //    }

  }
}

