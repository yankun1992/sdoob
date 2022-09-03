package tech.yankun.sdoob.driver.mysql.utils

import java.security.interfaces.RSAPublicKey
import java.security.spec.{InvalidKeySpecException, X509EncodedKeySpec}
import java.security.{InvalidKeyException, KeyFactory, NoSuchAlgorithmException, PublicKey}
import java.util.Base64
import javax.crypto.{BadPaddingException, Cipher, IllegalBlockSizeException, NoSuchPaddingException}

object RsaPublicKeyEncryptor {
  def encrypt(password: Array[Byte], nonce: Array[Byte], serverRsaPublicKey: String): Array[Byte] = {
    val rsaPublicKey = generateRsaPublicKey(serverRsaPublicKey)
    val obfuscatedPassword = obfuscate(password, nonce)
    encrypt(rsaPublicKey, obfuscatedPassword)
  }

  @throws[InvalidKeySpecException]
  @throws[NoSuchAlgorithmException]
  private def generateRsaPublicKey(serverRsaPublicKey: String) = {
    val content = serverRsaPublicKey.replace("-----BEGIN PUBLIC KEY-----", "")
      .replace("-----END PUBLIC KEY-----", "")
      .replaceAll("\\n", "")
    val key = Base64.getDecoder.decode(content.getBytes)
    val keySpec = new X509EncodedKeySpec(key)
    val keyFactory = KeyFactory.getInstance("RSA")
    keyFactory.generatePublic(keySpec).asInstanceOf[RSAPublicKey]
  }

  private def obfuscate(password: Array[Byte], nonce: Array[Byte]) = { // the password input can be mutated here
    for (i <- password.indices) {
      password(i) = (password(i) ^ nonce(i % nonce.length)).toByte
    }
    password
  }

  @throws[NoSuchAlgorithmException]
  @throws[NoSuchPaddingException]
  @throws[InvalidKeyException]
  @throws[IllegalBlockSizeException]
  @throws[BadPaddingException]
  private def encrypt(key: PublicKey, plainData: Array[Byte]) = {
    val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(plainData)
  }

}
