package tech.yankun.sdoob.driver.mysql.command

import tech.yankun.sdoob.driver.Command

class AuthenticationCommandBase(val username: String,
                                val password: String,
                                val database: String,
                                val collation: String) extends Command {

}
