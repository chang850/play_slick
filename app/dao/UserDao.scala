package dao

import javax.inject.Inject

import models.{Page, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]  {

  import profile.api._

  val Users = TableQuery[Users]

  class Users(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def username = column[String]("USERNAME")

    def email = column[String]("EMAIL")

    def password = column[String]("PASSWORD")

    def * = (id, username, password, email) <> (User.tupled, User.unapply)
  }

  def view(id: Long): Future[Option[User]] = {
    db.run(Users.filter(_.id === id).result.headOption)
  }

  def save(user: User): Future[Unit] = {
    db.run(Users += user).map(_ => ())
  }

  def list: Future[List[User]] = {
    db.run(Users.to[List].result)
  }

  def delete(id: Long): Future[Int] = {
    val user = Users.filter(_.id === id)
    val action = user.delete
    db.run(action)
  }

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
}
