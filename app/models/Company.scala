package models
import slick.jdbc.MySQLProfile.api._

case class Company(id: Long, name: String)

class Companys(tag: Tag) extends Table[Company](tag, "COMPANY") {
  def id = column[Long]("ID", O.AutoInc, O.PrimaryKey)
  def name = column[String]("NAME")
  def * = (id, name) <>(Company.tupled, Company.unapply)
}