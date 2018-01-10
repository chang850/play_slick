package vo

import models.Resource


//문제
//Search 는 하나
//ListVO 는 여러개
//그러면 VO 에 형태는 변경 되어 야한다.
//해당 전체를 넘기고 거기서 Seq 뽑아서 바인딩 하는 형태로 변경하자
case class ResourceListVO(keywordString: String, resourceList: Seq[Resource])

object ResourceListVO{
  case class SearchVO(keywordString : String)
}







