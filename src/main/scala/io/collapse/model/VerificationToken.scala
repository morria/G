package io.collapse.model

import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.twitter.util.Future
import com.twitter.util.Return
import com.twitter.util.Throw
import io.collapse.Crypto
import io.collapse.view.email.AccountVerificationHtmlView
import io.collapse.view.email.AccountVerificationTextView
import java.net.URLEncoder
import java.util.Date

case class VerificationToken(
  override val emailAddress:String,
  override val tokenHash:String,
  override val expires:Long)
extends Token(
  VerificationToken.tableName,
  emailAddress,
  tokenHash,
  expires) { }

object VerificationToken extends TokenObject with SES {
  override val tableName:String = "io.collapse.account_verification_tokens"
  override val expirationSeconds:Long = (60L * 60L * 24L * 7L)
  override val tokenBytes:Int = 32
  override val saltBytes:Int = 8 

  /**
   * Get a token and its associated Token model housing
   * its hash
   *
   * @return
   * A token value and its associated Token model
   */
  def apply(emailAddress:String) : Tuple2[String,VerificationToken] = {
    val now:Date = new Date();
    val tokenValue:String =
      Crypto.generateToken(tokenBytes)

    (tokenValue, VerificationToken(emailAddress,
      Crypto.saltedHash(tokenValue, saltBytes),
      now.getTime() + (expirationSeconds * 1000L)))
  }

  def sendVerificationEmail(emailAddress:String) : Future[Boolean] = {
    val (tokenValue:String, token:VerificationToken) =
      VerificationToken(emailAddress)

    token.store.transform {
      case Return(true) =>
        sendEmail(DefaultSourceEmailAddress,
          List(emailAddress),
          "Address Verification for collapse.io",
          verificationEmailBody(emailAddress, tokenValue))
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

  /**
   * Get an email message for sending a confirmation code
   */
  private def verificationEmailBody(emailAddress:String, tokenValue:String) : Body = {
    val body:Body = new Body()

    val verificationUrl =
      "https://g.collapse.io/verify" +
        "?email=" + URLEncoder.encode(emailAddress) +
        "&token=" + URLEncoder.encode(tokenValue)

    val textView =
      new AccountVerificationTextView(emailAddress, verificationUrl)

    val htmlView =
      new AccountVerificationHtmlView(emailAddress, verificationUrl)

    body.withText(new Content(textView.render))
    body.withHtml(new Content(htmlView.render))
  }
}
