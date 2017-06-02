package models

import org.specs2.mutable._


object ExecSpec extends Specification{

  "Mutations are isolated" should {
    var x = 0
    "x equals 1 if we set it." in {
      x = 1
      x mustEqual 1
    }
    "x is the default value if we don't change it" in {
      x mustEqual 0
    }
  }
}
