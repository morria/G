package io.collapse.model

import collection.JavaConversions._
import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.model.AttributeValue
import com.amazonaws.services.dynamodb.model.DeleteItemRequest
import com.amazonaws.services.dynamodb.model.DeleteItemResult
import com.amazonaws.services.dynamodb.model.GetItemRequest
import com.amazonaws.services.dynamodb.model.GetItemResult
import com.amazonaws.services.dynamodb.model.Key
import com.amazonaws.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.dynamodb.model.PutItemResult
import com.twitter.util.Future
import com.twitter.util.Return
import com.twitter.util.Throw
import io.collapse.Crypto
import java.util.Date
import scala.collection.Map

case class Token(
  tableName:String,
  emailAddress:String,
  tokenHash:String,
  expires:Long ) extends DynamoDb {

  def store : Future[Boolean] =
    put(tableName,
      Map(
        Token.FieldEmailAddress -> emailAddress,
        Token.FieldTokenHash -> tokenHash,
        Token.FieldExpires -> expires.toString));

  def delete : Future[Boolean] =
    delete(tableName, emailAddress)
}

object Token {
  val FieldEmailAddress:String = "email_address";
  val FieldTokenHash:String = "token_hash";
  val FieldExpires:String = "expires"
}

trait TokenObject extends DynamoDb {

  implicit val tableName:String
  implicit val expirationSeconds:Long
  implicit val tokenBytes:Int = 32
  implicit val saltBytes:Int = 8

  val fields:List[String] = 
    Token.FieldTokenHash ::
    Token.FieldExpires :: Nil

  def find(emailAddress:String) : Future[Option[Token]] =
    get(tableName, emailAddress, fields).transform {
      case Return(Some(map:Map[String,String])) =>
        Future.value(Some(
          Token(tableName,
            emailAddress,
            map(Token.FieldTokenHash),
            map(Token.FieldExpires).toLong )))
      case Return(None) =>
        Future.value(None)
      case Throw(throwable:Throwable) => 
        Future.exception(throwable)
    }

  /**
   * Iff the given token is legit, delete it
   */
  def invalidate(emailAddress:String, tokenValue:String)
    : Future[Boolean] = {
    find(emailAddress).transform {
      case Return(Some(token:Token)) =>
        if (Crypto.matches(tokenValue, token.tokenHash)) {
          token.delete
        } else {
          Future.value(false)
        }
      case Return(None) =>
        Future.value(false)
      case Throw(throwable:Throwable) => 
        Future.exception(throwable)
    }
  }

  def isValid(emailAddress:String, tokenValue:String)
  : Future[Boolean] = {
    val now:Date = new Date();

    find(emailAddress).transform {
      case Return(Some(token:Token)) =>
        Future.value(
          Crypto.matches(tokenValue, token.tokenHash) &&
          now.getTime() <= token.expires)
      case Return(None) =>
        Future.value(false)
      case Throw(throwable:Throwable) => 
        Future.exception(throwable)
    }
  }

  def exists(emailAddress:String) : Future[Boolean] =
    find(emailAddress).transform {
      case Return(Some(token:Token)) =>
        Future.value(true)
      case Return(None) =>
        Future.value(false)
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
}
