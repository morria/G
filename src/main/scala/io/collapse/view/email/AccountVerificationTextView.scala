package io.collapse.view.email

import com.twitter.finatra.View

class AccountVerificationTextView(val emailAddress:String, val verificationUrl:String) extends View {
  val template = "tmpl/email/account_verification_text.mustache"
}
