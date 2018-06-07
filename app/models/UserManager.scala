package models


import javax.inject._
import play.api.Logger
import scala.collection.immutable.HashMap

case class User (email: String, password: String, age: Option[Int] = Option.empty, description: Option[String] = Option.empty)

@Singleton
class UserManager @Inject()(){
  

   implicit def toOption[E](value : E) = Some(value)
   
   var users = List[User](User("email1@gmx.de","pw1",23,"adsfdescription"),User("email2@gmx.de","pw2"),User("email3@gmx.de","pw3"))
   
  def loginUser(email: String, password:String) : Option[User] = {
     
//     db.withConnection { implicit c =>
//      val result: Boolean = SQL("Select * from test").execute()
//      Logger.debug(String.valueOf(result))
//      }
     
     for(user <- users if(user.email.equals(email)&&user.password.equals(password)))
     {
      return user//works because of our implicit toOption definition
     }
     
     return null//returns optional.empty because of our implicit
  }
  
   def getUserByEmail(email:String) : Option[User] = {
     return users.find(user=>user.email.equals(email))
   }
   
  def signUp(email: String, password: String): Option[User] = {
    if(password.isEmpty()){
      return null
    }
    if(users.map(user=>user.email).contains(email)){
     return null
    }
    val createdUser = User(email,password)
    users.::(createdUser)
    return createdUser
  }
  
  
}