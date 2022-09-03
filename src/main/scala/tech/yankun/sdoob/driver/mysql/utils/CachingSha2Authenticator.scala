package tech.yankun.sdoob.driver.mysql.utils

import java.security.{MessageDigest, NoSuchAlgorithmException}

object CachingSha2Authenticator {
  def encode(password: Array[Byte], nonce: Array[Byte]): Array[Byte] = {
    val messageDigest = try {
      MessageDigest.getInstance("SHA-256")
    } catch {
      case e: NoSuchAlgorithmException => throw new RuntimeException(e)
    }

    // SHA256(password)
    val passwordHash1 = messageDigest.digest(password)
    messageDigest.reset()

    // SHA256(SHA256(password))
    val passwordHash2 = messageDigest.digest(passwordHash1)
    messageDigest.reset()

    //  SHA256(SHA256(SHA256(password)), Nonce)
    messageDigest.update(passwordHash2)
    val passwordDigest = messageDigest.digest(nonce)

    // result = passwordHash1 XOR passwordDigest
    for (i <- 0 until passwordHash1.length) {
      passwordHash1(i) = (passwordHash1(i) ^ passwordDigest(i)).toByte
    }
    passwordHash1
  }
}
