package models
import javax.inject._
import java.util.Date
import java.util.HashMap
import scala.Tuple1
import java.util.Calendar
import play.api.db._
import play.Logger

case class Tweet(tweetID: Int, emailAuthor:String, content:String, date:String, reTweet: Option[Tweet])

@Singleton
class TweetManager @Inject() (userManager:UserManager, db: Database){
  
  
  
 // private var tweets = List[Tweet](tweet1,tweet2,tweet3,tweet4)
  
//  var followsdb = List[Tuple2[String,String]](new Tuple2("email1@gmx.de","email2@gmx.de"))

  /**
   * Gets all news tweets (TODO: only the latest 10)
   */
  def getNewsFeed(userMail: String) : List[Tweet]={
    var feedTweets = List[Tweet]()
     val resultset = userManager.runMyQuery(
       s"""
       Select id as sqlid, author as sqlauthor, content as sqlcontent, date as sqldate, retweet as sqlretweet from tweets
       join follows on follows.followee = tweets.author
       where follows.follower='$userMail'
       union
       Select id as sqlid, author as sqlauthor, content as sqlcontent, date as sqldate, retweet as sqlretweet from tweets
       where tweets.author='$userMail'
       """)
     while(resultset.next){
       val tweetid = resultset.getInt("sqlid");
       val authorMail = resultset.getString("sqlauthor");
       val content= resultset.getString("sqlcontent");
       val date = resultset.getString("sqldate");
       val retweet = resultset.getInt("sqlretweet");
       feedTweets = feedTweets.::(Tweet(tweetid, authorMail, content, date, getTweetChain(retweet)));
    }
    //val allFollowedUsers = follows.filter(p=>p._1.equals(userMail)).map(tup => tup._2)
    //return tweets.filter(tweet=> allFollowedUsers.contains(tweet.emailAuthor))
    return feedTweets;
  }
  
  def getTweetChain(tweetID : Int) : Option[Tweet]={
    if(tweetID.equals("NULL")){
      return Option.empty;
    }
    val resultset = userManager.runMyQuery(
       s"""
       Select author as sqlauthor, content as sqlcontent, date as sqldate, retweet as sqlretweet from tweets
       where tweets.id = '$tweetID'
       """)
       resultset.last();
    if(resultset.getRow() <1){
       return Option.empty
     }
    val authorMail = resultset.getString("sqlauthor");
    val content= resultset.getString("sqlcontent");
    val date = resultset.getString("sqldate");
    val retweet = resultset.getInt("sqlretweet");
    return Option.apply(Tweet(tweetID, authorMail, content, date,getTweetChain(retweet)));
    
  }
  
  def follow(follows:String,toFollow:String){
     val resultset = userManager.runMyQuery(
       s"""
       Select * from follows
       where follows.follower='follows' and  follows.followee='$toFollow'
       """)
    resultset.last();
     if(resultset.getRow() <1){
       userManager.runMyUpdate(s"""
       Insert into follows(follower, followee)
       values('$follows', '$toFollow')
       """)
     }
  }
    def unfollow(follows:String,toUnfollow:String){
      Logger.debug("unfollow with "+ follows+""+ toUnfollow);
     val resultset = userManager.runMyQuery(
       s"""
       Select * from follows
       where follows.follower='$follows' and  follows.followee='$toUnfollow'
       """)
       resultset.last();
     Logger.debug(""+resultset.getRow());
     if(resultset.getRow() == 1){
      Logger.debug("deleting row "+ follows, toUnfollow);

       userManager.runMyUpdate(s"""
       delete from follows
       where follows.follower = '$follows' and follows.followee='$toUnfollow'
       """)
     }
  }
  def getTweetById(tweetId : Int) : Option[Tweet] ={
    getTweetChain(tweetId);
  }
  
  def addTweet(content:String, author:String, retweet: Option[Tweet]){
    if(retweet.isDefined){
    var retweetId : Int = retweet.get.tweetID;
    Logger.debug("nonempty retweet : "+ retweetId)
    userManager.runMyUpdate(s"""
      insert into  tweets (author, content, date, retweet)
      values ('$author', '$content', CURRENT_TIMESTAMP,'$retweetId')
      """)
    }
    else{
      Logger.debug("empty retweet .. ")
    userManager.runMyUpdate(s"""
      insert into  tweets (author, content, date, retweet)
      values ('$author', '$content', CURRENT_TIMESTAMP, NULL)
      """)
    }
  }
  
  def getUsersThatFollowYou(you: String):List[User]={
    var followers = List[String]();
    val resultSet = userManager.runMyQuery(s"""
      Select follower as sqlfollower from follows
      where follows.followee='$you'
      """)
      while(resultSet.next)
      {
        followers = followers.::(resultSet.getString("sqlfollower"));
      }
    followers.map(mail => userManager.getUserByEmail(mail)).filter(p => p.isDefined).map(p=>p.get);
  }
  
  def getUsersThatYouFollow(you: String):List[User]={
    var followers = List[String]();
    val resultSet = userManager.runMyQuery(s"""
      Select followee as sqlfollowee from follows
      where follows.follower='$you'
      """)
      while(resultSet.next)
      {
        followers = followers.::(resultSet.getString("sqlfollowee"));
      }
    followers.map(mail => userManager.getUserByEmail(mail)).filter(p => p.isDefined).map(p=>p.get);
  }  
}