package io.collapse.model

import io.collapse.Crypto
import java.util.Date
import scala.collection.Map

case class AuthenticationToken(
  override val emailAddress:String,
  override val tokenHash:String,
  override val expires:Long)
extends Token(
  AuthenticationToken.tableName,
  emailAddress,
  tokenHash,
  expires) { }

object AuthenticationToken extends TokenObject {
  override val tableName:String = "io.collapse.authentication_tokens"
  override val expirationSeconds:Long = (60L * 60L * 24L * 365L)
  override val tokenBytes:Int = 32
  override val saltBytes:Int = 8

  /**
   * Get a token and its associated Token model housing
   * its hash
   *
   * @return
   * A token value and its associated Token model
   */
  def apply(emailAddress:String) : Tuple2[String,AuthenticationToken] = {
    val now:Date = new Date();
    val tokenValue:String =
      Crypto.generateToken(tokenBytes)

    (tokenValue, AuthenticationToken(emailAddress,
      Crypto.saltedHash(tokenValue, saltBytes),
      now.getTime() + (expirationSeconds * 1000L)))
  }
}
