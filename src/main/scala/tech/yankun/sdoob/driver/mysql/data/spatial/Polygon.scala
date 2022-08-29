package tech.yankun.sdoob.driver.mysql.data.spatial

import scala.beans.BeanProperty

/**
 * A Polygon is a planar Surface representing a multisided geometry. It is defined by a single exterior boundary
 * and zero or more interior boundaries, where each interior boundary defines a hole in the Polygon.
 *
 * @param SRID
 * @param lineStrings
 */
class Polygon(SRID: Long, @BeanProperty var lineStrings: List[LineString]) {

  def this(other: Polygon) {
    this(other.SRID, other.lineStrings)
  }

}
