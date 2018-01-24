package forms

import play.api.data.Form
import play.api.data.Forms._



object ResourceSearchForm {
  val form = Form(
    mapping(
      "keywordString" -> text,
      "optionSelect" -> text,
      "radioInput" -> text
    )(vo.ResourceVO.SearchVO.apply)(vo.ResourceVO.SearchVO.unapply)
  )
}


