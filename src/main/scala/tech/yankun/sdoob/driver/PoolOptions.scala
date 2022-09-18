package tech.yankun.sdoob.driver

import scala.beans.BeanProperty

class PoolOptions extends Serializable {
  @BeanProperty var size: Int = _
}

object PoolOptions {

}