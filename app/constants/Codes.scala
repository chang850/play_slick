package constants

class Codes extends Enumeration {

  object ResourceType {
    val READY = Value("ready")
    val SET = Value("set")
    val GO = Value("go")
  }

  object TaskStatus {
    val ready = Value("ready")
    val set = Value("set")
    val go = Value("go")
  }
}
