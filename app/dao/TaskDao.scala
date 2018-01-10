package dao

import javax.inject.Inject

import constants.TaskStatus
import models.Task
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class TaskDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]
{
  import profile.api._

  val Tasks = TableQuery[TasksTable]

  class TasksTable(tag: Tag) extends Table[Task](tag, "TASK") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def color = column[String]("COLOR")

    def status = column[TaskStatus.Value]("STATUS")

    def project = column[Long]("PROJECT")

    def * = (id, color, status, project) <> (Task.tupled, Task.unapply)
  }

  implicit val taskStatusColumnType = MappedColumnType.base[TaskStatus.Value, String](
    _.toString, string => TaskStatus.withName(string)
  )


  def findById(id: Long): Future[Task] =
    db.run(Tasks.filter(_.id === id).result.head)

  def findByColor(color: String): DBIO[Option[Task]] =
    Tasks.filter(_.color === color).result.headOption

  def findByProjectId(projectId: Long): Future[List[Task]] =
    db.run(Tasks.filter(_.project === projectId).to[List].result)

  def findByReadyStatus: DBIO[List[Task]] =
    Tasks.filter(_.status === TaskStatus.ready).to[List].result


  def partialUpdate(id: Long, color: Option[String], status: Option[TaskStatus.Value], project: Option[Long]): Future[Int] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val query = Tasks.filter(_.id === id)

    val update = query.result.head.flatMap { task =>
      query.update(task.patch(color, status, project))
    }

    db.run(update)
  }

  def all(): DBIO[Seq[Task]] =
    Tasks.result

  def insert(Task: Task): DBIO[Long] =
    Tasks returning Tasks.map(_.id) += Task

  def _deleteAllInProject(projectId: Long): DBIO[Int] =
    Tasks.filter(_.project === projectId).delete
}