package io.collapse

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64

object Crypto {

  private lazy val random:SecureRandom =
    SecureRandom.getInstance("SHA1PRNG")

  /**
   * Turn a password into a base64 encoded salted hash
   *
   * @param value
   * The value to produce a salted hash for
   *
   * @param bytes
   * The number of salt bytes
   */
  def saltedHash(value:String, bytes:Int) : String = {
    val salt:Array[Byte] = new Array[Byte](bytes)
    random.nextBytes(salt)
    saltedHash(value, salt)
  }

  /**
   * Turn a password into a base64 encoded salted hash
   *
   * @param value
   * The value to produce a salted hash for
   *
   * @param bytes
   * The number of salt bytes
   */
  private def saltedHash(value:String, salt:Array[Byte]) : String = {
    val digest:MessageDigest =
      MessageDigest.getInstance("SHA-512")

    digest.reset()
    digest.update(salt)

    base64Encode(salt) + base64Encode(digest.digest(value.getBytes("UTF-8")))
  }

  def hexMd5(value:String) : String = {
    hexEncode(MessageDigest.getInstance("MD5").digest(value.getBytes))
  }

  /**
   * Get an authentication token
   */
  def generateToken(bytes:Int) : String = {
    val tokenBytes:Array[Byte] = new Array[Byte](bytes)
    random.nextBytes(tokenBytes)
    base64Encode(tokenBytes);
  }

  /**
   *
   */
  def matches(value:String, hash:String) : Boolean = {
    val hashBytes : Array[Byte] = base64Decode(hash)
    val salt : Array[Byte] = hashBytes.slice(0, 8)
    hash.equals(saltedHash(value, salt))
  }

  /**
   */
  def base64Encode(data : Array[Byte]) : String =
    new String(Base64.encodeBase64(data))

  /**
   * Decode a Base64 String
   */
  def base64Decode(data:String) : Array[Byte] =
    Base64.decodeBase64(data)

  def hexEncode(bytes:Array[Byte]) : String = {
    val stringBuffer:StringBuffer =
      new StringBuffer();

    bytes.foreach {
      byte:Byte =>
        stringBuffer.append(Integer.toHexString(
          (byte & 0xFF) | 0x100).substring(1,3))
    }

    stringBuffer.toString();
  }

}
