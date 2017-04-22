package io.collapse

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.finatra.test._
import io.collapse._

class AppSpec extends SpecHelper {
  val app = new G.CollapseController
}
