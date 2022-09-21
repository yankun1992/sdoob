package tech.yankun.sdoob.utils

object Utils {
  def string2Boolean(string: String): Boolean = string.toLowerCase.trim match {
    case "false" => false
    case "true" => true
    case _ => false
  }

}
