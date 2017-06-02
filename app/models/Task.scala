package models

import constants.TaskStatus


case class Task(id: Long, color: String, status: TaskStatus.Value, project: Long) {
  def patch(color: Option[String], status: Option[TaskStatus.Value], project: Option[Long]): Task =
    this.copy(color = color.getOrElse(this.color),
              status = status.getOrElse(this.status),
              project = project.getOrElse(this.project))
}


