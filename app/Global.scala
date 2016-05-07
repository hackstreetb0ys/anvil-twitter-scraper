import javax.inject.Named

import Actors.TwitterStreamActor.Follow
import akka.actor.ActorRef
import com.google.inject.{Inject, Singleton}

/**
  * Created by rag on 07/05/2016.
  */

trait GlobalTrait {}

@Singleton
class Global @Inject()(@Named("twitter-stream") twitter: ActorRef) extends GlobalTrait{
  val start = twitter ! Follow(Set("apple","taylor swift"))
}
