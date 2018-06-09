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


class ProfileController @Inject() (cc: ControllerComponents, tweetManager : TweetManager, userManager:UserManager) 
extends AbstractController(cc) 
with play.api.i18n.I18nSupport{
  
  //TODO create seperate profile template with a follow button.. solves 2 problems
  
  
  def yourFollows() = Action { implicit request: Request[AnyContent] =>
    val sessionMail = request.session.get("email")
    if(sessionMail.isDefined){
    Ok(views.html.followPage(userManager.allUsers(),
        tweetManager.getUsersThatYouFollow(sessionMail.get),
        tweetManager.getUsersThatFollowYou(sessionMail.get)))
    }
    else{
    Redirect(routes.HomeController.index())
    }
  }
  
  def followUser(userMailToFollow: String)= Action { implicit request: Request[AnyContent] =>
    val optMail = request.session.get("email")
    if(optMail.isDefined){
      tweetManager.follow(optMail.get, userMailToFollow);
      Redirect(routes.ProfileController.yourFollows())
    }
    else{
      Redirect(routes.HomeController.index())
    }
  }
}