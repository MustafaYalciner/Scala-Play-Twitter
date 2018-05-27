package models
import javax.inject._

case class Tweet(emailAuthor:String, content:String, date:String, reTweet: Option[Tweet])

@Singleton
class TweetManager @Inject() (){
  //map email -> tweets
  //
}