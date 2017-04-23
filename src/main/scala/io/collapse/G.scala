package io.collapse

import com.twitter.finatra.ContentType._
import com.twitter.finatra._
import com.twitter.util.Future
import com.twitter.util.Return
import com.twitter.util.Throw
import io.collapse.exception._
import io.collapse.model.CookieSet.fromFinagleCookieSet
import io.collapse.model.TokenCookie.fromCookie
import io.collapse.model._
import io.collapse.view._
import io.collapse.view.email._
import io.collapse.view.error._
import org.jboss.netty.handler.codec.http.Cookie

object G {

  class CollapseController extends Controller {

    get("/") { request =>
			authenticatedAs(request) match {
        case Some(emailAddress:String) =>
					renderViewFuture(Main(emailAddress))
        case None =>
          renderViewFuture(Splash())
      }
    }

    get("/:name") { request =>
			authenticatedAs(request) match {
        case Some(emailAddress:String) =>
          val name:String = request.routeParams.getOrElse("name", "")
          Link.find(emailAddress, name).transform {
						case Return(Some(link:Link)) =>
							redirectTemporary(link.url)
						case Return(None) =>
							renderViewFuture(CreateLinkView(emailAddress, name));
						case Throw(exception:Exception) =>
              Future.exception(exception)
					}
				case None =>
					redirectTemporary("/")
			}
    }

    get("/search/:name") { request =>
			authenticatedAs(request) match {
        case Some(emailAddress:String) =>
          val name:String = request.routeParams.getOrElse("name", "")
          Link.find(emailAddress, name).transform {
						case Return(Some(link:Link)) =>
							redirectTemporary(link.url)
						case Return(None) =>
							redirectTemporary("https://www.google.com/search?q=" + name)
						case Throw(exception:Exception) =>
              Future.exception(exception)
					}
				case None =>
					redirectTemporary("/")
			}
    }

    post("/:name") { request => 
			authenticatedAs(request) match {
        case Some(emailAddress:String) =>
          val name:String = request.routeParams.getOrElse("name", "")
          val url:String = request.params.getOrElse("url", "")
          Link(emailAddress, name, url).store.transform {
            case Return(true) =>
              renderJson(Map("success" -> true))
            case Return(false) =>
              renderJson(Map("success" -> false))
            case Throw(exception:Exception) =>
              Future.exception(exception)
          }
				case None =>
					redirectTemporary("/")
			}
    }

    get("/api/send-verification") { request =>
      val emailAddress:String =
        request.params.get("email").getOrElse("");
        VerificationToken.sendVerificationEmail(emailAddress).transform {
          case Return(true) =>
            renderJson(Map("success" -> true))
          case Throw(exception:Exception) =>
            Future.exception(exception)
        }
    }

    get("/verify") { request =>
      val emailAddress:String = request.params.get("email").getOrElse("");
      val tokenValue:String = request.params.get("token").getOrElse("");
      if ("".equals(emailAddress) || "".equals(tokenValue))
        Future.exception(new InvalidParameters)
      else
        VerificationToken.isValid(emailAddress, tokenValue).transform {
          case Return(true) =>
            val (authenticationTokenValue:String, authenticationToken:AuthenticationToken) = AuthenticationToken(emailAddress);
            authenticationToken.store.transform {
              case Return(success:Boolean) =>
                VerificationToken.invalidate(emailAddress, tokenValue).transform {
                  case Return(true) =>
                    render
                      .plain("")
                      .status(302)
                      .header("Set-Cookie", "token="+emailAddress+":"+authenticationTokenValue)
                      .header("Location", "/")
                      .toFuture
                  case Throw(exception:Exception) =>
                    Future.exception(exception)
                  case _ =>
                    renderViewFuture(new VerificationSentFail())
                }
              case Throw(exception:Exception) =>
                Future.exception(exception)
              }
          case Return(false) =>
            renderViewFuture(new VerificationSentFail())
          case Throw(exception:Exception) =>
            Future.exception(exception)
        }
    }

    private def redirectTemporary(location:String) : Future[Response] =
      render.plain("").status(302).header("Location", location).toFuture

    private def renderView(view:View) : Response =
      render.view(view)

    private def renderViewFuture(view:View) : Future[Response] =
      renderView(view).toFuture

    private def renderJson(map:Map[String,Any]) : Future[Response] =
      render.json(map).toFuture

    /**
     * Get the emailAddress of the person that we are
     * authenticated as, or None.
     *
     * n.b.: This method blocks while we confirm token information
     *
     * @return
     * An option for an emailAddress for which this
     * member is authenticated
     */
    private def authenticatedAs(request:Request) : Option[String] =
      request.cookies.get("token") match {
        case Some(cookie:Cookie) =>
          val tokenCookie:TokenCookie = fromCookie(cookie)
          AuthenticationToken.isValid(tokenCookie.emailAddress, tokenCookie.token).get match {
            case true =>
              Some(tokenCookie.emailAddress)
            case _ => {
              logger.error("Invalid Token")
              None
            }
          }
        case None => None
      }

    error { request =>
      request.error match {
        case Some(e:DoNotLog) => Nil
        case Some(exception:Exception) =>
          logger.error(exception, exception.getMessage());
        case _ =>
          logger.error("Unknown error")
      }
      request.error match {
        case Some(e:PermissionDenied) =>
          redirectTemporary("/");
        case _ =>
          render.status(500).view(new Error500()).toFuture
      }
    }

    notFound { request =>
      render.status(404).view(new Error404()).toFuture
    }

  }

  val app = new CollapseController

  def main(args: Array[String]) = {
    GFinatraServer.register(app)
    GFinatraServer.start()
  }
}
