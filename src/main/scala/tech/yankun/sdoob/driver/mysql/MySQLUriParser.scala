package tech.yankun.sdoob.driver.mysql

object MySQLUriParser {
  private val SCHEME_DESIGNATOR_REGEX = "jdbc:(mysql|mariadb)://" // URI scheme designator
  private val USER_INFO_REGEX = "((?<userinfo>[a-zA-Z0-9\\-._~%!*]+(:[a-zA-Z0-9\\-._~%!*]*)?)@)?" // user name and password
  private val NET_LOCATION_REGEX = "(?<netloc>[0-9.]+|\\[[a-zA-Z0-9:]+]|[a-zA-Z0-9\\-._~%]+)?" // ip v4/v6 address, host, domain socket address
  private val PORT_REGEX = "(:(?<port>\\d+))?" // port
  private val SCHEMA_REGEX = "(/(?<schema>[a-zA-Z0-9\\-._~%!*]+))?" // schema name
  private val ATTRIBUTES_REGEX = "(\\?(?<attributes>.*))?" // attributes

  private val FULL_URI_PATTERN = ("^" + SCHEME_DESIGNATOR_REGEX + USER_INFO_REGEX + NET_LOCATION_REGEX +
    PORT_REGEX + SCHEMA_REGEX + ATTRIBUTES_REGEX + "$").r

  def parse(uri: String): MySQLConnectOptions = {
    val options = new MySQLConnectOptions()
    uri match {
      case FULL_URI_PATTERN(dbtype, userinfo, netloc, port, schema) =>
        println(dbtype)
      case _ =>
    }

    options
  }

  def main(args: Array[String]): Unit = {
    val uri = "jdbc:mysql://yankun:password@database.server.com:3306/mydb"
    MySQLUriParser.parse(uri)
  }

}
