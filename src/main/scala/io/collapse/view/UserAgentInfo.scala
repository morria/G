package io.collapse.view

import net.sf.uadetector.UserAgent

trait UserAgentInfo {

  implicit val userAgentOption:Option[UserAgent]

  val operatingSystemFamily:String =
    userAgentOption match {
      case Some(userAgent:UserAgent) =>
        userAgent.getOperatingSystem().getFamilyName()
      case None => ""
    }

  val operatingSystem:Map[String,Boolean] =
    List("OS X", "iOS", "Windows", "Android", "unknown").map {
      os:String =>
        (os.replaceAll(" ", "_") + "?", os.equals(operatingSystemFamily))
    }.toMap

  val operatingSystemUnknown:Map[String,Boolean] =
    Map("?" -> !operatingSystem.toList
      .map { a => a._2 }
      .reduce { (a,b) => a || b })
 }
