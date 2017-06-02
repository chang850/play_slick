package controllers

import javax.inject.Inject

import dao.{CompanyDao, TeamDao}
import play.mvc.Controller


class CompanyController @Inject()(companyDao: CompanyDao, teamDao: TeamDao) extends Controller {

}
