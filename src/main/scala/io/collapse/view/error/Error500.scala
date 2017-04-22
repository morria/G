package io.collapse.view.error

import com.twitter.finatra.View

class Error500() extends View() {
  val template = "tmpl/pages/error/500.mustache"
}
