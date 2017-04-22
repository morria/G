package io.collapse.model

case class EmailAddress(val emailAddress:String) {
  val Email = """^(.+)@([\w\.]+)$""".r
}
