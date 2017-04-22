package io.collapse.view.email

import com.twitter.finatra.View

class AccountVerificationHtmlView(val emailAddress:String, val verificationUrl:String) extends View {
  val template = "tmpl/email/account_verification_html.mustache"
}
