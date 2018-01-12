package forms

import dao.BaseEntity
import play.api.data.Form
import play.api.data.Forms._

object ResourceAddForm {
  val form = Form(
    mapping(
      "resource" -> mapping(
        "id" -> ignored(0L),
        "resourceKey" -> nonEmptyText,
        "resourceName" -> nonEmptyText
      )(BaseEntity.Resource.apply)(BaseEntity.Resource.unapply),
      "resourceDetailList" -> seq[BaseEntity.ResourceDetail](
        mapping(
          "id" -> ignored(0L),
          "resourceText" -> nonEmptyText,
          "resourceLocale" -> nonEmptyText,
          "resourceKey" -> ignored(0L)
        )(BaseEntity.ResourceDetail.apply)(BaseEntity.ResourceDetail.unapply))
    )(Data.apply)(Data.unapply)
  )

  case class Data(
      resource: BaseEntity.Resource,
      resourceDetailList: Seq[BaseEntity.ResourceDetail]
  )
}
