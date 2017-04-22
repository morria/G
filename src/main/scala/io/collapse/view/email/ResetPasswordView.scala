package io.collapse.view.email

import com.twitter.finatra.View
import java.net.URLEncoder

abstract class ResetPasswordView(emailAddress:String, tokenValue:String) extends View {
  val resetLink =
    "https://collapse.io/reset" +
    "?email=" + URLEncoder.encode(emailAddress) +
    "&token=" + URLEncoder.encode(tokenValue)
}
