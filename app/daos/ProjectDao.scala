package daos

import javax.inject.Inject

import constants.TaskStatus
import models.{Project, Task}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ProjectDao @Inject()(taskDAO: TaskDao)(protected val dbConfigProvider: DatabaseConfigProvider) (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//  val db = dbConfig.db

  import profile.api._

  val Projects = TableQuery[ProjectsTable]

  class ProjectsTable(tag: Tag) extends Table[Project](tag, "PROJECT") {

    def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (id, name) <> (Project.tupled, Project.unapply)
  }

  private def _findById(id: Long): DBIO[Option[Project]] =
    Projects.filter(_.id === id).result.headOption

  private def _findByName(name: String): Query[ProjectsTable, Project, List] =
    Projects.filter(_.name === name).to[List]

  def findById(id: Long): Future[Option[Project]] =
    db.run(_findById(id))

  def findByName(name: String): Future[List[Project]] =
    db.run(_findByName(name).result)

  def addTask(color: String, projectId: Long): Future[Long] = {
    val interaction = for {
      Some(project) <- _findById(projectId)
      id <- taskDAO.insert(Task(0, color, TaskStatus.ready, project.id))
    } yield id

    db.run(interaction.transactionally)
  }

  def list: Future[List[Project]] = {
    db.run(Projects.to[List].result)
  }

  def save(name: String): Future[Long] = {
    val project = Project(0, name)
    db.run(Projects returning Projects.map(_.id) += project)
  }

  def delete(name: String): Future[Int] = {
    val query = _findByName(name)

    val interaction = for {
      projects <- query.result
      _ <- DBIO.sequence(projects.map(p => taskDAO._deleteAllInProject(p.id)))
      projectsDeleted <- query.delete
    } yield projectsDeleted

    db.run(interaction.transactionally)
  }
}
