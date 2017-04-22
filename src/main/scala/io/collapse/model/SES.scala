package io.collapse.model

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.amazonaws.services.simpleemail.model.SendEmailResult
import com.twitter.util.Future
import com.twitter.util.FutureTask
import com.twitter.util.Return
import com.twitter.util.Throw
import scala.collection.JavaConversions._

trait SES {
  val DefaultSourceEmailAddress:String = "hello@collapse.io"

  /**
   * A handle on SES with credentials fed by the
   * environment variables
   *
   *     AWS_ACCESS_KEY_ID
   *     AWS_SECRET_KEY
   */
  protected lazy val simpleEmailService:AmazonSimpleEmailServiceClient =
    new AmazonSimpleEmailServiceClient(new EnvironmentVariableCredentialsProvider());

  def sendEmail(sourceAddress:String,
    targetAddressList:List[String],
    subject:String,
    messageBody:Body)
  : Future[Boolean] = {

    val destination:Destination =
      new Destination(targetAddressList);

    val message:Message =
      new Message(new Content(subject), messageBody)

    val task:FutureTask[SendEmailResult] = FutureTask({
      simpleEmailService
        .sendEmail(new SendEmailRequest(
          sourceAddress, destination, message))
    })
      
    task.run
      
    task.transform {
      case Return(sendEmailResult:SendEmailResult) =>
        Future.value(true)
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

}
