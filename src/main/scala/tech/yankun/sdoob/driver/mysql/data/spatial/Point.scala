package tech.yankun.sdoob.driver.mysql.data.spatial

import scala.beans.BeanProperty

class Point(SRID: Long = 0, @BeanProperty var x: Double = 0, @BeanProperty var y: Double = 0) extends Geometry(SRID) {
  def this(other: Point) {
    this(SRID = other.SRID, other.x, other.y)
  }
}
