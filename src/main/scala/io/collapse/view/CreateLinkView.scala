package io.collapse.view

import com.twitter.finatra.View

case class CreateLinkView(val emailAddress:String, val name:String) extends View {
  val template = "tmpl/pages/create/index.mustache"
}
