package tech.yankun.sdoob.driver

/**
 * Contains static metadata about the backend database server
 */
trait DatabaseMetadata {
  /** @return The product name of the backend database server */
  def productName: String

  /**
   * @return The full version string for the backend database server.
   *         This may be useful for for parsing more subtle aspects of the version string.
   *         For simple information like database major and minor version, use [[majorVersion]]
   *         and [[minorVersion]] instead.
   */
  def fullVersion: String

  /** @return The major version of the backend database server */
  def majorVersion: Int

  /** @return The minor version of the backend database server */
  def minorVersion: Int
}
