package tech.yankun.sdoob.driver

trait ClientPool {
  def waitReadyClient(): Client = {
    ???
  }
}

object ClientPool {
  def createPoolByUri(uri: String, clients: Int = 16): ClientPool = {
    ???
  }
}