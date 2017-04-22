package io.collapse.model

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.model.AttributeAction
import com.amazonaws.services.dynamodb.model.AttributeValue
import com.amazonaws.services.dynamodb.model.AttributeValueUpdate
import com.amazonaws.services.dynamodb.model.DeleteItemRequest
import com.amazonaws.services.dynamodb.model.DeleteItemResult
import com.amazonaws.services.dynamodb.model.DescribeTableRequest
import com.amazonaws.services.dynamodb.model.DescribeTableResult
import com.amazonaws.services.dynamodb.model.GetItemRequest
import com.amazonaws.services.dynamodb.model.GetItemResult
import com.amazonaws.services.dynamodb.model.Key
import com.amazonaws.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.dynamodb.model.PutItemResult
import com.amazonaws.services.dynamodb.model.ScanRequest
import com.amazonaws.services.dynamodb.model.ScanResult
import com.amazonaws.services.dynamodb.model.TableDescription
import com.amazonaws.services.dynamodb.model.UpdateItemRequest
import com.amazonaws.services.dynamodb.model.UpdateItemResult
import com.twitter.util.Future
import com.twitter.util.FutureTask
import com.twitter.util.Return
import com.twitter.util.Throw
import java.util.HashMap
import scala.collection.JavaConverters._
import scala.collection.Map

trait DynamoDb {

  /**
   * A handle on dynamoDB with credentials fed by the
   * environment variables
   *
   *     AWS_ACCESS_KEY_ID
   *     AWS_SECRET_KEY
   */
  protected lazy val dynamoDb:AmazonDynamoDBClient =
    new AmazonDynamoDBClient(new EnvironmentVariableCredentialsProvider());

  /**
   * Put an item in DynamoDB
   */
  protected def put(tableName:String, map:Map[String,String])
  : Future[Boolean] = {

    val hashMap:HashMap[String,AttributeValue] =
      new HashMap[String,AttributeValue]();

    map
      .filter {
        fields:Tuple2[String,String] =>
          (null != fields._2 && !"".equals(fields._2))
      }
      .foreach {
        case (key:String, value:String) =>
          hashMap.put(key, new AttributeValue(value))
      }

    val task:FutureTask[PutItemResult] = FutureTask({
      dynamoDb.putItem(new PutItemRequest(tableName, hashMap))
    })

    task.run

    task.transform {
      case Return(putItemResult:PutItemResult) =>
        Future.value(true)
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

  protected def update(tableName:String, keyValue:String, map:Map[String, String], action:AttributeAction)
  : Future[Boolean] = {

    val key:Key = new Key(new AttributeValue(keyValue))

    val hashMap:HashMap[String,AttributeValueUpdate] =
      new HashMap[String,AttributeValueUpdate]();

    map
      .filter {
        fields:Tuple2[String,String] =>
          (null != fields._2 && !"".equals(fields._2))
      }
      .foreach {
        case (key:String, value:String) =>
          hashMap.put(key,
            new AttributeValueUpdate(new AttributeValue(value), action))
      }

    val updateItemRequest:UpdateItemRequest =
      new UpdateItemRequest(tableName, key, hashMap)

    val task:FutureTask[UpdateItemResult] = FutureTask({
      dynamoDb.updateItem(updateItemRequest)
    })

    task.run

    task.transform {
      case Return(updateItemResult:UpdateItemResult) =>
        Future.value(true)
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

  /**
   * Get a value from DynamoDB
   */
  protected def get(tableName:String, keyValue:String, fields:List[String])
  : Future[Option[Map[String,String]]] = {

    val task:FutureTask[GetItemResult] = FutureTask({
      dynamoDb.getItem(
        new GetItemRequest(tableName,
          new Key(new AttributeValue(keyValue))))
    })

    task.run

    task.transform {
      case Return(getItemResult:GetItemResult) =>
        Future.value(getItemResult.getItem() match {
          case map:java.util.Map[String,AttributeValue] =>
            Some(map.asScala.mapValues { _.getS() })
          case _ => None
        })
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
      case _ => Future.value(None)
    }
  }

  /**
   * Delete an entry
   */
  protected def delete(table:String, keyValue:String)
  : Future[Boolean] = {
    val task:FutureTask[DeleteItemResult] = FutureTask({
      dynamoDb.deleteItem(new DeleteItemRequest( table,
        new Key(new AttributeValue(keyValue))))
    })

    task.run

    task.transform {
      case Return(deleteItemResult:DeleteItemResult) =>
        Future.value(true)
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

  protected def scan(table:String, fields:List[String], limit:Int, fromKey:Option[String])
  : Future[List[scala.collection.Map[String,String]]] = {

    val task:FutureTask[ScanResult] = FutureTask({
      val scanRequest:ScanRequest = new ScanRequest(table);
      scanRequest.setLimit(limit)
      fromKey match {
        case Some(keyValue:String) =>
          val key:Key = new Key(new AttributeValue(keyValue))
          scanRequest.setExclusiveStartKey(key)
        case _ => null
      }
      dynamoDb.scan(scanRequest)
    })

    task.run

    task.transform {
      case Return(scanResult:ScanResult) =>
        Future.value(scanResult.getItems.asScala.toList.map {
          item:java.util.Map[String, AttributeValue] =>
            item.asScala.mapValues {
              attributeValue:AttributeValue =>
                attributeValue.getS()
            }
          })
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

  protected def count(table:String) : Future[Long] = {
    val task:FutureTask[DescribeTableResult] = FutureTask({
      val describeTableRequest:DescribeTableRequest =
        new DescribeTableRequest();
      describeTableRequest.setTableName(table);
      dynamoDb.describeTable(describeTableRequest)
    })

    task.run

    task.transform {
      case Return(describeTableResult:DescribeTableResult) =>
        val tableDescription:TableDescription =
          describeTableResult.getTable()
        Future.value(tableDescription.getItemCount())
      case Throw(throwable:Throwable) =>
        Future.exception(throwable)
    }
  }

}
