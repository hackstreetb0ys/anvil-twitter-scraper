import javax.inject.Named

import Actors.TwitterStreamActor.{Campaign, Follow}
import akka.actor.ActorRef
import com.google.inject.{Inject, Singleton}

trait GlobalTrait {}

@Singleton
class Global @Inject()(@Named("twitter-stream") twitter: ActorRef) extends GlobalTrait{
    twitter ! Campaign("In", Follow(Set("@strongerin", "#strongerin", "@britinfluence",
    "#britishinfluence", "#betteroffin", "#yestoeu", "#votestay",
    "#remainineu", "#incampaign", "#yes2eu", "#notobrexit", "#no2brexit")))

    twitter ! Campaign("Out", Follow(Set("@voteleave", "@vote_leave", "@LeaveEUOfficial", "#VoteLeave", "#Brexit",
    "#brexitnow", "#LeaveEU", "leave the eu", "#betteroffout", "#no2eu", "#brexitnow",
    "#leavecampaign", "#freeeuropefromtheeu", "#loveeuropehatetheeu", "#loveeuropenoteu", "#stoptheeu")))
}
