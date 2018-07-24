package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.User
import play.api.Logger
import scala.collection.immutable.HashMap
import models.TweetManager
import models.UserManager
import models.Tweet

case class CreateTweet(content:String);

@Singleton
class HomeController @Inject()(cc: ControllerComponents, 
                               tweetManager : TweetManager, 
                               userManager: UserManager) 
extends AbstractController(cc) 
with play.api.i18n.I18nSupport{
  

  
  val tweetForm = Form(
  mapping(
    "content" -> text    
  )
  (CreateTweet.apply)(CreateTweet.unapply))

  def index() = Action { implicit request: Request[AnyContent] =>  
    Ok(views.html.index())
  }
  
  def carefullyParseInt(toBeParsed : String) : Option[Int] = {
    try{
      Option.apply(toBeParsed.toInt);
    }
    catch{
      case e: IllegalArgumentException => {Logger.debug("String value could not be parsed to int:  " + toBeParsed);
      Option.empty
      }
    }
  }
  
  
  def retweet(tweetIdStr : String) = Action { implicit request: Request[AnyContent] =>
    val retweeting = {
      if(carefullyParseInt(tweetIdStr).isEmpty){
        Option.empty
      }
      else{
        tweetManager.getTweetById(carefullyParseInt(tweetIdStr).get)
      }
    }
    val sessionMail = request.session.get("email")
    if(sessionMail.isDefined){
      Ok(views.html.retweetPage(tweetForm,retweeting))
    }
    else{
    Redirect(routes.HomeController.index())
    }
  }
  def tweet() = Action { implicit request: Request[AnyContent] =>
    val sessionMail = request.session.get("email")
    if(sessionMail.isDefined){
      Ok(views.html.retweetPage(tweetForm,Option.empty))
    }
    else{
    Redirect(routes.HomeController.home())
    }
  }
  
  def tweetPost(reTweetID : String = "") = Action { implicit request =>
    val retweet : Option[Tweet]= {
      if(carefullyParseInt(reTweetID).isEmpty){
        Option.empty
      }
      else{
        tweetManager.getTweetById(carefullyParseInt(reTweetID).get)
      }
    }
   if(request.session.get("email").isEmpty){
     Redirect(routes.HomeController.index())
   }
   tweetForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.retweetPage(formWithErrors,Option.empty)),
      createTweet => {
        tweetManager.addTweet(createTweet.content,request.session.get("email").get,retweet)
      Redirect(routes.HomeController.index())}
        )
  }
  
  def tweetPosttest() = Action { implicit request =>
   if(request.session.get("email").isEmpty){
     Redirect(routes.HomeController.index())
   }
   tweetForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.retweetPage(formWithErrors,Option.empty)),
      createTweet => {
        tweetManager.addTweet(createTweet.content,request.session.get("email").get,Option.empty)
      Redirect(routes.HomeController.index())}
        )
  }
  
  def home() = Action { implicit request: Request[AnyContent] =>
    val sessionMail = request.session.get("email")
    if(sessionMail.isDefined){
    Ok(views.html.home(tweetManager.getNewsFeed(sessionMail.get),userManager.getUserByEmail(sessionMail.get).get))
    }
    else{
    Redirect(routes.HomeController.index())
    } 
  }
}
