package io.collapse

import com.twitter.finagle.SimpleFilter
import com.twitter.finagle.http.{Request => FinagleRequest}
import com.twitter.finagle.http.{Response => FinagleResponse}
import com.twitter.finatra.Controller
import com.twitter.finatra.FinatraServer
import com.twitter.logging.Logger
import com.twitter.logging.Policy
import com.twitter.logging.config.FileHandlerConfig
import com.twitter.logging.config.LoggerConfig

object GFinatraServer {
  var server:GFinatraServer = new GFinatraServer

  def register(app:Controller) {
    server.register(app)
  }

  def start() {
    server.start()
  }

  def addFilter(filter:SimpleFilter[FinagleRequest, FinagleResponse]) {
    server.addFilter(filter)
  }
}

class GFinatraServer extends FinatraServer {
  override def initLogger() {
    val config = new LoggerConfig {
      node = "g.collapse.io"
      level = Logger.ALL
      handlers = new FileHandlerConfig {
        filename = "logs/collapse.log"
        roll = Policy.SigHup
        level = Logger.ALL
      }
    }
    config()
  }
}
