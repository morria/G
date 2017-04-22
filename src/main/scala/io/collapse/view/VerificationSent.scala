package io.collapse.view

import com.twitter.finatra.View

case class VerificationSent extends View {
  val template = "tmpl/pages/verification_sent/index.mustache"
}
