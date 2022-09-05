package tech.yankun.sdoob.examples

import tech.yankun.sdoob.driver.PoolOptions
import tech.yankun.sdoob.driver.mysql.{MySQLConnectOptions, MySQLPool}

object MySQLExample {
  def main(args: Array[String]): Unit = {
    val connectOptions = MySQLConnectOptions.fromUri("jdbc:mysql://yankun:7890@localhost:3306/test")
    val poolOptions = new PoolOptions()
    poolOptions.setSize(1)
    val pool = new MySQLPool(poolOptions, connectOptions)


  }
}
