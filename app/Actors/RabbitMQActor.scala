package Actors

import Actors.RabbitMQActor.StatusMessage
import Actors.TwitterStreamActor.Campaign
import akka.actor.{Actor, Props}
import com.google.inject.Inject
import com.rabbitmq.client.ConnectionFactory
import play.Environment
import play.api.libs.json.{JsArray, JsObject, JsString, Json}

import scala.util.parsing.json.JSONObject


class RabbitMQActor @Inject() (env: Environment) extends Actor{

  val factory = new ConnectionFactory()
  factory.setHost("rabbit")
  factory.setUsername("user")
  factory.setPassword("password")
  val connection = factory.newConnection()
  val channel = connection.createChannel()
  val args =  new java.util.HashMap[String, Object]()

  args.put("x-max-queue-length", new Integer(10000))
  channel.queueDeclare("tweets", false, false, false, args)
  channel.queueDeclare("campaigns", false, false, false, args)

  override def receive: Receive = {
    case StatusMessage(msg, campaigns) =>
      campaigns.keys.foreach({tags =>
        var matchedTags = List[String]()
        tags.foreach({tag =>
          if (msg.toLowerCase().contains(tag.toLowerCase)) {
            matchedTags = tag :: matchedTags
            val tweetQueueMessage = JsObject(Seq(
              "id" -> JsString(tag),
              "tweet" -> JsString(msg)
            ))
            channel.basicPublish("", "tweets", null, tweetQueueMessage.toString().getBytes())
          }
        })
        if (matchedTags.nonEmpty) {
          val campaignQueueMessage = JsObject(Seq(
            "campaign" -> JsString(campaigns(tags)),
            "tags" -> JsArray(matchedTags.map(JsString(_))),
            "message" -> JsString(msg)
          ))
          channel.basicPublish("", "campaigns", null, campaignQueueMessage.toString().getBytes())
        }
      })
  }

  override def postStop() = {
    channel.close()
    connection.close()
  }
}

object RabbitMQActor{
  val props = Props[RabbitMQActor]
  case class StatusMessage(message: String, campaigns: Map[Set[String], String])
}