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
import models.UserManager
import models.UserManager


@Singleton
class HomeController @Inject()(cc: ControllerComponents, userManager : UserManager) extends AbstractController(cc) with play.api.i18n.I18nSupport{

  def index() = Action { implicit request: Request[AnyContent] =>   
    Ok(views.html.index())
  }
      
  def home() = Action { implicit request: Request[AnyContent] =>
    if(request.session.get("email").isDefined){
    Ok(views.html.home())
    }
    else{
    Redirect(routes.HomeController.index())
    } 
  }
}
