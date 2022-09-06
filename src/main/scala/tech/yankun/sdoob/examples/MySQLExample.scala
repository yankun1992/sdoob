package tech.yankun.sdoob.examples

import tech.yankun.sdoob.driver.PoolOptions
import tech.yankun.sdoob.driver.command.SimpleQueryCommand
import tech.yankun.sdoob.driver.mysql.{MySQLConnectOptions, MySQLPool}

object MySQLExample {
  def main(args: Array[String]): Unit = {
    val connectOptions = MySQLConnectOptions.fromUri("jdbc:mysql://yankun:7890@localhost:3306/test")
    val poolOptions = new PoolOptions()
    poolOptions.setSize(16)
    val pool = new MySQLPool(poolOptions, connectOptions)
    val start = System.currentTimeMillis()
    for (elem <- 0 to 10000) {
      val values = (elem * 10000 until (elem + 1) * 10000).map(i => s"(${i})").mkString(",")
      pool.getReadyClient() match {
        case Some(client) =>
          client.write(SimpleQueryCommand(s"insert into test(id) values ${values}"))
        case None =>
      }
    }
    val end = System.currentTimeMillis()
    println(s"time spend ${(end - start) / 1000}")


  }
}
