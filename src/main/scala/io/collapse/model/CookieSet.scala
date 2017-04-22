package io.collapse.model

import org.jboss.netty.handler.codec.http.Cookie
import com.twitter.finagle.http.{CookieSet => FinagleCookieSet}

class CookieSet(val cookieSet:FinagleCookieSet) {
  lazy val map:Map[String,Cookie] =
    cookieSet.iterator.toList.map {
      cookie:Cookie => (cookie.getName(), cookie)
    } toMap

  /**
   * Get a cookie value by name
   */
  def get(name:String) : Option[Cookie] = map.get(name)
}

object CookieSet {
  def apply(cookieSet:FinagleCookieSet) : CookieSet = {
    new CookieSet(cookieSet)
  }

  implicit def fromFinagleCookieSet(cookieSet:FinagleCookieSet) : CookieSet = {
    CookieSet(cookieSet)
  }
}
