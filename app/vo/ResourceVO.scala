package vo

// ResourceList => List vo
case class ResourceVO(resourceKey: String, resourceName: String, resourceLocale: Option[String], resourceText: Option[String])

object ResourceVO {
  case class SearchVO(keywordString: String = "", optionSelect: String = "", radioInput : String)
}
