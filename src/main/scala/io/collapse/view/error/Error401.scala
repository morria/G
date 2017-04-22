package io.collapse.view.error

import com.twitter.finatra.View

class Error401() extends View() {
  val template = "tmpl/pages/error/401.mustache"
}
