package tech.yankun.sdoob.driver.mysql.data.spatial

import scala.beans.BeanProperty

/**
 * A MultiPoint is a geometry collection composed of Point elements. The points are not connected or ordered in any way.
 *
 * @param SRID
 * @param points
 */
class MultiPoint(SRID: Long, @BeanProperty var points: List[Point]) extends Geometry(SRID) {

  def this(other: MultiPoint) {
    this(other.SRID, other.points)
  }

}
