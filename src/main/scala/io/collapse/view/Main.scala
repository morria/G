package io.collapse.view

import com.twitter.finatra.View

case class Main(val emailAddress:String) extends View {
  val template = "tmpl/pages/main/index.mustache"
}
