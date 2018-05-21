package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.LoginData
import play.api.Logger
import scala.collection.immutable.HashMap

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport{

  var users = new HashMap[String,String]()
  
  val loginForm = Form(
      tuple(
          "email" -> text,
          "password" -> text)
      verifying("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
)

val signupForm = Form(
      tuple(
          "email" -> text,
          "password" -> text,
          "age"     -> text)
          verifying("email adress already used", result => result match{
            case(email, password, age) => true
          })
)

def checkAlreadyUsed(email: String) : Boolean=  {
   return users.get(email).isDefined
  }

def check(email: String, password: String) = {
    Logger.debug("checking up and down"+(users.contains(email) && users.get(email).get.eq(password)))
    Logger.debug(users.toString())
    Logger.debug(email + password)
    users.exists((tuple) =>tuple._1.equals(email)&&tuple._2.equals(password))
  }
  
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
   Logger.debug("logger working test")
    Ok(views.html.index())
  }
    
    def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }
  
      def signup() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signup(signupForm))
  }
      
  def restricted() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.restrictedArea())
  }
  
  def logout() = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.index()).withNewSession
  }
      
 def authenticate = Action { implicit request =>
   Logger.debug("authentication method")
   loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.HomeController.restricted()).withSession("email" -> user._1)
        )
    
  }
 
 def addUser = Action { implicit request =>
   Logger.debug("addUser method")
   signupForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.signup(formWithErrors)),
      user => {
        users = users.updated(user._1, user._2)
        Redirect(routes.HomeController.restricted()).withSession("email" -> user._1)}
        )
    
  }
      
}
