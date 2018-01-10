package vo

case class ResourceVO(resourceKey: String, resourceName: String, resourceLocale: String, resourceText: String)

object ResourceVO {
  case class SearchVO(keywordString: String = "", optionSelect: String = "", radioInput : String)
}
