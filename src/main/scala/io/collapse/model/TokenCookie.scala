package io.collapse.model

import java.net.URLDecoder
import org.jboss.netty.handler.codec.http.Cookie

case class TokenCookie(val emailAddress:String, val token:String) { }

object TokenCookie {
  implicit def fromCookie(cookie:Cookie) : TokenCookie = {
    val Array(emailAddress:String, token) =
      URLDecoder.decode(cookie.getValue().replace("+", "%2B"), "UTF-8").replace("%2B", "+").split(":")
    TokenCookie(emailAddress, token)
  }
}
