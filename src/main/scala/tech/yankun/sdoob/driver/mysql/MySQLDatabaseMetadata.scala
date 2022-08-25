package tech.yankun.sdoob.driver.mysql

import tech.yankun.sdoob.driver.DatabaseMetadata

class MySQLDatabaseMetadata(override val fullVersion: String,
                            override val productName: String,
                            override val majorVersion: Int,
                            override val minorVersion: Int,
                            val microVersion: Int) extends DatabaseMetadata {

}

object MySQLDatabaseMetadata {
  
  def parse(serverVersion: String): MySQLDatabaseMetadata = {
    var majorVersion = 0
    var minorVersion = 0
    var microVersion = 0
    val len = serverVersion.length
    val isMariaDb = serverVersion.contains("MariaDB")
    val productName = if (isMariaDb) "MariaDB"
    else "MySQL"
    var versionToken: String = null
    val versionTokenStartIdx = if (isMariaDb) 6 else 0 // MariaDB server version is by default prefixed by "5.5.5-"
    var versionTokenEndIdx = versionTokenStartIdx
    var break = false
    while (versionTokenEndIdx < len && !break) {
      val c = serverVersion.charAt(versionTokenEndIdx)
      if (c == '-' || c == ' ') {
        versionToken = serverVersion.substring(versionTokenStartIdx, versionTokenEndIdx)
        break = true
      }
      versionTokenEndIdx += 1
    }

    if (versionToken == null) { // if there's no '-' char
      versionToken = serverVersion
    }
    // we assume the server version tokens follows the syntax: ${major}.${minor}.${micro}
    val versionTokens = versionToken.split("\\.")
    try {
      majorVersion = versionTokens(0).toInt
      minorVersion = versionTokens(1).toInt
      microVersion = versionTokens(2).toInt
    } catch {
      case ex: Exception =>
      // make sure it does fail the connection phase
    }
    new MySQLDatabaseMetadata(serverVersion, productName, majorVersion, minorVersion, microVersion)
  }
}