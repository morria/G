package io.collapse.view

import com.twitter.finatra.View

case class Splash extends View {
  val template = "tmpl/pages/splash/index.mustache"
}
