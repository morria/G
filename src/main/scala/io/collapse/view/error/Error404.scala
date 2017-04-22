package io.collapse.view.error

import com.twitter.finatra.View

class Error404() extends View() {
  val template = "tmpl/pages/error/404.mustache"
}
