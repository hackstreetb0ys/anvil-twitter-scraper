package Actors

import Actors.RabbitMQActor.StatusMessage
import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.rabbitmq.client.ConnectionFactory
import scala.collection.JavaConversions._


class RabbitMQActor extends Actor{

  private val QUEUE_NAME = "tweets"
  val factory = new ConnectionFactory()
  factory.setHost("192.168.99.100")
  factory.setUsername("user")
  factory.setPassword("password")
  val connection = factory.newConnection()
  val channel = connection.createChannel()
  val args =  new java.util.HashMap[String, Object]()

  args.put("x-max-queue-length", new Integer(10000))
  channel.queueDeclare(QUEUE_NAME, false, false, false, args)

  override def receive: Receive = {
    case StatusMessage(msg) =>
      channel.basicPublish("", QUEUE_NAME, null, msg.getBytes())
  }

  override def postStop() = {
    channel.close()
    connection.close()
  }
}

object RabbitMQActor{
  val props = Props[RabbitMQActor]
  case class StatusMessage(message: String)
}