package io.collapse.view

import com.twitter.finatra.View

case class VerificationSentFail extends View {
  val template = "tmpl/pages/verification_sent/invalid.mustache"
}

