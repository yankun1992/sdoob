package tech.yankun.sdoob.driver

trait ClientPool {
  def waitReadyClient(): Client = {
    ???
  }
}

object ClientPool {
  def createPoolByUri(uri: String, poolOptions: PoolOptions): ClientPool = {
    ???
  }

  def apply(connectOptions: SqlConnectOptions, poolOptions: PoolOptions): ClientPool = ???
}