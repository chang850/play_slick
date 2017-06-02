package controllers

import javax.inject.Inject

import base.Secured
import dao.{ProjectDao, TaskDao}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

class ProjectController @Inject()(projectDAO: ProjectDao, taskDAO: TaskDao , val messagesApi: MessagesApi) extends Controller with Secured with I18nSupport {

  def addTaskToProject(color: String, projectId: Long) = Action.async {
    implicit rs =>
      projectDAO.addTask(color, projectId).map { _ =>
        Redirect(routes.ProjectController.view(projectId))
      }
  }

  def modifyTask(taskId: Long, color: Option[String]) = Action.async {
    implicit rs =>
      taskDAO
        .partialUpdate(taskId, color, None, None)
        .map(i => Ok(s"Rows affected : $i"))
  }

  def save(name: String) = Action.async { implicit rs =>
    projectDAO.save(name).map(id => Ok(s"project $id created"))
  }

  def view(id: Long) = Action.async { implicit rs =>
    for {
      Some(project) <- projectDAO.findById(id)
      tasks <- taskDAO.findByProjectId(id)
    } yield Ok(views.html.project.view(project, tasks))
  }

  def list = Action.async { implicit rs =>
    projectDAO.list.map(projects => Ok(views.html.project.list(projects)))
  }

  def delete(name: String) = Action.async { implicit rs =>
    projectDAO.delete(name).map(num => Ok(s"$num projects deleted"))
  }
}
