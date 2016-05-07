package Actors

import javax.inject.{Inject, Named}

import Actors.RabbitMQActor.StatusMessage
import Actors.TwitterStreamActor._
import akka.actor.{Actor, ActorRef, Props}
import com.google.inject.Singleton
import twitter4j._

import collection.JavaConverters._

@Singleton
class  TwitterStreamActor @Inject() (@Named("mq-actor") mqActor: ActorRef) extends Actor {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey("DV4xYaeuEvCiwveS1dk8Ezaga")
    .setOAuthConsumerSecret("7F7ZARaJnydZiRvWcDg0Vlnzr3alpgmLk7txQaWQiCRdpglbfp")
    .setOAuthAccessToken("542708952-bvMyuUYBi50FovsbalJP2cpA7qLT2ZpETBFnXvDi")
    .setOAuthAccessTokenSecret("3JyDVvrz1jkomIUYvRRWFbhboYG54rRgeCBUFsVVI9qKg")
    .build
  val twitterStream = new TwitterStreamFactory(config).getInstance
  twitterStream.addListener(listener)
  var tags: Set[String]  = Set()
  var campaigns: Map[ Set[String], String] = Map()


  override def receive = {
    case Follow(newTags) =>
      tags = tags ++ newTags
      println("starting")
      restartAPI()

    case Unfollow(oldtag) =>
      tags = tags - oldtag
      restartAPI()
    case Campaign(name, follows) =>
      campaigns = campaigns + (follows.tags -> name)
      tags = tags ++ follows.tags
      restartAPI()
  }

  private def restartAPI() = {
    if (tags.nonEmpty){
      val query = new FilterQuery()
      query.track(tags.toArray:_*)
      twitterStream.filter(query)
      println(s"updating query ${tags.toString()}")
    } else {
      twitterStream.cleanUp()
      twitterStream.shutdown()
    }
  }

  override def postStop(): Unit = {
    twitterStream.cleanUp()
    twitterStream.shutdown()
  }

  def listener = new StatusListener() {
    def onStatus(status: Status) { println(s"got tweet ${status.getText}"); mqActor ! StatusMessage(status.getText, campaigns)}
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }

}

object TwitterStreamActor {
  def props = Props[TwitterStreamActor]
  //State
  trait State
  case class Clean() extends State
  case class Connected() extends State

  //messages
  case class Follow(tags: Set[String])
  case class Unfollow(tag: String)
  case class Campaign(name: String, follows: Follow)
}
