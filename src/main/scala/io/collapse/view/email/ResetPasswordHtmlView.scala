package io.collapse.view.email

import com.twitter.finatra.View

class ResetPasswordHtmlView(emailAddress:String, token:String) extends ResetPasswordView(emailAddress, token) {
  val template = "tmpl/email/reset_password_html.mustache"
}
