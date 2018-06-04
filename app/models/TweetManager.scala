package models
import javax.inject._
import java.util.Date
import java.util.HashMap
import scala.Tuple1
import java.util.Calendar

case class Tweet(tweetID: Int, emailAuthor:String, content:String, date:Date, reTweet: Option[Tweet])

@Singleton
class TweetManager @Inject() (){
  
  val tweet1 = Tweet(1,"email1@gmx.de","contentcontent",new Date(2018,5,29),Option.empty)
  val tweet2 = Tweet(2,"email2@gmx.de","content2 asdfsadfsadfsaf sadf",new Date(2018,5,27),Option.apply(tweet1))
  val tweet3 = Tweet(3,"email2@gmx.de","content3 asdfsffffffffffffffadf",new Date(2018,5,27),Option.apply(tweet2))
  val tweet4 = Tweet(4,"email2@gmx.de","content4 asdaaaaaaaaaaaaaaadfsaf sadf",new Date(2018,5,27),Option.apply(tweet3))
  
  
  private var tweets = List[Tweet](tweet1,tweet2,tweet3,tweet4)
  
  var follows = List[Tuple2[String,String]](new Tuple2("email1@gmx.de","email2@gmx.de"))

  /**
   * Gets all news tweets (TODO: only the latest 10)
   */
  def getNewsFeed(userMail: String) : List[Tweet]={
    //val allFollowedUsers = follows.filter(p=>p._1.equals(userMail)).map(tup => tup._2)
    //return tweets.filter(tweet=> allFollowedUsers.contains(tweet.emailAuthor))
    return tweets;
  }
  
  def getTweetById(tweetId : Int) : Option[Tweet] ={
    //TODO log if more than one tweet can by found with the given id.
    return tweets.find(tweet=> tweet.tweetID.equals(tweetId))
  }
  
  def addTweet(content:String, author:String, retweet: Option[Tweet]){
    tweets = tweets.::(Tweet(tweets.length,author, content, Calendar.getInstance.getTime(),retweet))
  }
  
  
  /**
   * funktionen, die gewährleistet werden müsse:
   * -newsfeed eines nutzers zurückgeben:
   * 		-liste derjedigen, die aboniert wurden -> deren tweets
   * -zuordnung von nutzer zu tweets.
   * -tweets hinzufügen
   * -
   */
  
  
}