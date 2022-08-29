package tech.yankun.sdoob.driver.mysql.data.spatial

import scala.beans.BeanProperty

/**
 * A MultiLineString is a MultiCurve geometry collection composed of LineString elements.
 *
 * @param SRID
 * @param lineStrings
 */
class MultiLineString(SRID: Long = 0, @BeanProperty var lineStrings: List[LineString]) extends Geometry(SRID) {
  def this(other: MultiLineString) {
    this(other.SRID, other.lineStrings)
  }
}
