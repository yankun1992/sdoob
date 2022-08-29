package tech.yankun.sdoob.driver.mysql.data.spatial

import scala.beans.BeanProperty

/**
 * A MultiPolygon is a MultiSurface object composed of Polygon elements.
 *
 * @param SRID
 * @param polygons
 */
class MultiPolygon(SRID: Long, @BeanProperty var polygons: List[Polygon]) {
  def this(other: MultiPolygon) {
    this(other.SRID, other.polygons)
  }
}
