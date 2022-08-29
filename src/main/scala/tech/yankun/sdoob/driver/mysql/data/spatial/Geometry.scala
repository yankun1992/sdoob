package tech.yankun.sdoob.driver.mysql.data.spatial

/**
 * Geometry is an abstract class which represents the base of MySQL geometry data type.
 *
 * @param SRID
 */
abstract class Geometry(private var SRID: Long) {

  def this(other: Geometry) {
    this(0)
    this.SRID = other.SRID
  }

  def getSRID: Long = SRID

  def setSRID(SRID: Long): this.type = {
    this.SRID = SRID
    this
  }
}
