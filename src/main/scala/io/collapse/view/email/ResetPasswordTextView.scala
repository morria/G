package io.collapse.view.email

import com.twitter.finatra.View

class ResetPasswordTextView(emailAddress:String, token:String) extends ResetPasswordView(emailAddress, token) {
  val template = "tmpl/email/reset_password_text.mustache"
}
