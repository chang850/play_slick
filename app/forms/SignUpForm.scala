package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * The form which handles the sign up process.
  */
object SignUpForm {

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
      firstName: String,
      lastName: String,
      email: String,
      password: String
  )
}
