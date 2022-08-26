package tech.yankun.sdoob.driver.mysql

import fastparse.NoWhitespace._
import fastparse._

object MySQLUriParser {
  private def scheme_designator[_: P] = P("jdbc:" ~ ("mysql" | "mariadb").! ~ "://") // URI scheme designator

  private def username[_: P] = P(CharIn("a-zA-Z0-9\\-._~%!*").rep(1).!)

  private def password[_: P] = P(CharIn("a-zA-Z0-9\\-._~%!*").rep(0).!)

  private def user_info[_: P] = P(username ~ (":" ~ password).? ~ "@") // user name and password

  private def port[_: P] = P(":" ~ CharIn("0-9").rep.!.map(str => str.toInt)) // port

  private def ipv4[_: P] = P(CharsWhileIn("0-9").rep(min = 4, sep = ".").! ~ port.?)

  // TODO: fix match
  private def ipv6[_: P] = P(CharsWhileIn("a-zA-Z0-9\\-._~%").rep(1, sep = ":").! ~ port.?)

  private def domain[_: P] = P(CharsWhile(c => c != '.' && c != ':').rep(min = 1, sep = ".").! ~ port.?)

  private def net_location[_: P] = P(ipv4 | domain) // ip v4/v6 address, host, domain socket address

  private def schema[_: P] = P("/" ~ CharIn("a-zA-Z0-9\\-._~%!*").rep(1).!) // schema name

  private def attribute[_: P] =
    P(CharsWhileIn("a-zA-Z0-9_~%!*").! ~ "=" ~ CharsWhile(_ != '&').!)

  private def attributes[_: P] = P("?" ~ attribute.rep(min = 0, sep = P("&"), max = Int.MaxValue)) // attributes

  def url[_: P]: P[(String, Option[(String, Option[String])], (String, Option[Int]), Option[String], Option[Seq[(String, String)]])] =
    P(Start ~ scheme_designator ~ user_info.? ~ net_location ~ schema.? ~ attributes.? ~ End)

  def parse(uri: String): MySQLConnectOptions = {
    val options = new MySQLConnectOptions()
    fastparse.parse(uri, url(_)) match {
      case Parsed.Success(value, index) =>
        val (designator, user, (address, port), schema, attributes) = value
        user.foreach { case (name, pass) =>
          options.setUser(name)
          pass.foreach(p => options.setPassword(p))
        }
        options.setHost(address)
        port.foreach(p => options.setPort(p))
        schema.foreach(sc => options.setDatabase(sc))
        attributes.foreach(att => att.foreach { case (key, value) => options.addProperty(key, value) })
        println(designator)
      case failure: Parsed.Failure =>
        throw new IllegalArgumentException(s"the JDBC URL format is incorrect with: ${failure.msg}")
    }

    options
  }

  def main(args: Array[String]): Unit = {

    val attributedString = "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=true&rewriteBatchedStatements=true"
    val atts = fastparse.parse(attributedString, attributes(_))
    val uri = "jdbc:mysql://yankun:password@database.server.com:3306/mydb" + attributedString
    MySQLUriParser.parse(uri)
  }

}
