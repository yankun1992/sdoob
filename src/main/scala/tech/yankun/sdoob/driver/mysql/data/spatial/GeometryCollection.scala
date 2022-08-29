package tech.yankun.sdoob.driver.mysql.data.spatial

/**
 * A GeomCollection is a geometry that is a collection of zero or more geometries of any class.
 *
 * @param SRID
 * @param geometries
 */
class GeometryCollection(SRID: Long = 0, private var geometries: List[Geometry] = List.empty) extends Geometry(SRID) {
  def this(other: GeometryCollection) {
    this(other.SRID, other.geometries)
  }

  def setGeometries(geometries: List[Geometry]): this.type = {
    this.geometries = geometries
    this
  }

  def getGeometries: List[Geometry] = this.geometries
}
