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


@Singleton
class HomeController @Inject()(cc: ControllerComponents, 
                               tweetManager : TweetManager, 
                               userManager: UserManager) 
extends AbstractController(cc) 
with play.api.i18n.I18nSupport{
  

  def index() = Action { implicit request: Request[AnyContent] =>   
    Ok(views.html.index())
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
