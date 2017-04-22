package io.collapse.model

import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.twitter.util.Future
import com.twitter.util.Return
import com.twitter.util.Throw
import io.collapse.Crypto
import io.collapse.view.email.AccountVerificationHtmlView
import io.collapse.view.email.AccountVerificationTextView
import java.net.URLEncoder
import java.util.Date

case class Link(
	val emailAddress:String,
  val name:String,
  val url:String
) extends DynamoDb with Copying[Link] {

  lazy val key = emailAddress + "|" + name;

  def store : Future[Boolean] = {
    put(Link.TableName,
      Map(
        Link.FieldKey -> key,
        Link.FieldUrl -> url
      )
    )
}

  def delete : Future[Boolean] = delete(Link.TableName, key)
}

object Link extends DynamoDb {
  val TableName:String = "io.collapse.links"

  val FieldKey:String = "key"
  val FieldUrl:String = "url"

	val Fields = FieldKey :: FieldUrl :: Nil

  def apply(key:String, url:String) : Link  = {
    val Key = """^(.+)\|(.+)$""".r;
    val Key(emailAddress:String, name:String) = key;
		new Link(emailAddress, name, url);
  }

  def find(emailAddress:String, name:String) : Future[Option[Link]] = {
		val key = emailAddress + "|" + name;

    get(TableName, key, Fields).transform {
      case Return(Some(map)) =>
        Future.value(Some(Link.fromMap(map.toMap)))
      case Return(None) =>
        Future.value(None)
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

	def fromMap(map:Map[String,String]):Link =
    Link(
      map(FieldKey),
      map.getOrElse(FieldUrl, "https://g.collapse.io")
    )

}
