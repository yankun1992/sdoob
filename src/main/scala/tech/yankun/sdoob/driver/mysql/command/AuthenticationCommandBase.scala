package tech.yankun.sdoob.driver.mysql.command

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.Command
import tech.yankun.sdoob.driver.mysql.MySQLCollation

class AuthenticationCommandBase(val username: String,
                                val password: String,
                                val database: String,
                                val collation: MySQLCollation, val serverRsaPublicKey: ByteBuf,
                                val connectionAttributes: Map[String, String]) extends Command {

}
