package tech.yankun.sdoob.driver.mysql.utils

import java.security.{MessageDigest, NoSuchAlgorithmException}

object Native41Authenticator {
  def encode(password: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    val messageDigest = try {
      MessageDigest.getInstance("SHA-1")
    } catch {
      case e: NoSuchAlgorithmException => throw new RuntimeException(e)
    }

    // SHA1(password)
    val passwordHash1 = messageDigest.digest(password)
    messageDigest.reset()

    // SHA1(SHA1(password))
    val passwordHash2 = messageDigest.digest(passwordHash1)
    messageDigest.reset()

    // SHA1("20-bytes random data from server" <concat> SHA1(SHA1(password))
    messageDigest.update(salt)
    messageDigest.update(passwordHash2)
    val passwordHash3 = messageDigest.digest
    // result = passwordHash1 XOR passwordHash3
    for (i <- 0 until passwordHash1.length) {
      passwordHash1(i) = (passwordHash1(i) ^ passwordHash3(i)).toByte
    }
    passwordHash1
  }
}
