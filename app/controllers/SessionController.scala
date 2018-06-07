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


class SessionController @Inject() (cc: ControllerComponents, userManager : UserManager) 
extends AbstractController(cc) 
with play.api.i18n.I18nSupport{
    
  var users = new HashMap[String,String]()
  
  val loginForm = Form(
      tuple(
          "email" -> text,
          "password" -> text)
      verifying("Invalid email or password", result => result match {
      case (email, password) => userManager.loginUser(email, password).isDefined}))

val signupForm = Form(
      tuple(
          "email" -> text,
          "password" -> text)
          verifying("email adress already used", result => result match{
            case(email, password) => userManager.signUp(email, password).isDefined}))
          

          
 def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }
  
 def signup() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signup(signupForm))
  }
      
 def logout() = Action {implicit request: Request[AnyContent] =>
      Redirect(routes.HomeController.index()).withNewSession

  }
 
   
 def authenticate = Action { implicit request =>
   Logger.debug("authentication method")
   loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.HomeController.index()).withSession("email" -> user._1)
        )
    
  }
 

 
 def addUser = Action { implicit request =>
   Logger.debug("addUser method")
   signupForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.signup(formWithErrors)),
      user => Redirect(routes.HomeController.index()).withSession("email" -> user._1)
        )
    
  }
 
}