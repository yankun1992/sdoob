package tech.yankun.sdoob.driver.mysql.data.spatial

import scala.beans.BeanProperty

/**
 * A LineString is a Curve with linear interpolation between points, it may represents a Line or a LinearRing.
 *
 * @param SRID
 * @param points
 */
class LineString(SRID: Long = 0, @BeanProperty var points: List[Point]) extends Geometry(SRID) {

  def this(other: LineString) {
    this(other.SRID, other.points)
  }

}
