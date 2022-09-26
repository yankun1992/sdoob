package tech.yankun.sdoob.examples

import tech.yankun.sdoob.driver.pg.{PGClient, PGConnectOptions}

object PGExample {
  def main(args: Array[String]): Unit = {
    testClient()
  }

  def testClient(): Unit = {
    val options = new PGConnectOptions()
    options.setHost("localhost")
    options.setPort(5432)
    options.setDatabase("postgres")
    options.setUser("postgres")
    options.setPassword("postgres")
    val client = new PGClient(options)
    client.connect()
    assert(client.isConnected)
    client.init()
    while (!client.isAuthenticated) {
      client.channelRead()
    }
  }
}
