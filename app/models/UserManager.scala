package models


import javax.inject._
import play.api.Logger
import scala.collection.immutable.HashMap
import play.api.db._
case class User (email: String, password: String, age: Option[Int] = Option.empty, description: Option[String] = Option.empty)

@Singleton
class UserManager @Inject()(db: Database){
  

   implicit def toOption[E](value : E) = Some(value)
   
  // var users = List[User](User("email1@gmx.de","pw1",23,"adsfdescription"),User("email2@gmx.de","pw2"),User("email3@gmx.de","pw3"))
   
  def loginUser(email: String, password:String) : Option[User] = {
   Logger.debug("Connection to database"+db.getConnection().toString())
     val connection = db.getConnection()
     val statement = connection.createStatement()
     val query = s"SELECT EMAIL as sqlemail, PASSWORD as sqlpassword, AGE as sqlage, DESCRIPTION as sqldescription FROM USERS WHERE USERS.EMAIL='$email' and USERS.PASSWORD='$password'"
     val resultset = statement.executeQuery(query)
     //find out the rowcount
     resultset.last();
     if(resultset.getRow() <1){
       return Option.empty
     }
     else if(resultset.getRow()>1){
       Logger.debug("apparently email is not primary key in database, an email request returned more than one result.")
       return Option.empty;
     }
     else{
       val email = resultset.getString("sqlemail");
       val password = resultset.getString("sqlpassword");
       val age = resultset.getInt("sqlage");
       val description = resultset.getString("sqldescription");
       Logger.debug("email for found loginstuff  "+email)
       return User(email, password, age, description);
     }
//     Logger.debug(resultset.toString())
     
 //    for(user <- users if(user.email.equals(email)&&user.password.equals(password)))
 //    {
 //     return user//works because of our implicit toOption definition
  //   }
     
   //  return null//returns optional.empty because of our implicit
  }
 
   def getUserByEmail(email:String) : Option[User] = {
      Logger.debug("Connection to database"+db.getConnection().toString())
     val connection = db.getConnection()
     val statement = connection.createStatement()
     val query = s"SELECT EMAIL as sqlemail, PASSWORD as sqlpassword, AGE as sqlage, DESCRIPTION as sqldescription FROM USERS WHERE USERS.EMAIL='$email'"
     val resultset = statement.executeQuery(query)
     //find out the rowcount
     resultset.last();
     if(resultset.getRow() <1){
       return Option.empty
     }
     else if(resultset.getRow()>1){
       Logger.debug("apparently email is not primary key in database, an email request returned more than one result.")
       return Option.empty;
     }
     else{
       val email = resultset.getString("sqlemail");
       val password = resultset.getString("sqlpassword");
       val age = resultset.getInt("sqlage");
       val description = resultset.getString("sqldescription");
       Logger.debug("email for found loginstuff  "+email)
       return User(email, password, age, description);
     }
    // return users.find(user=>user.email.equals(email))
   }
   
  def signUp(email: String, password: String): Option[User] = {
    if(password.isEmpty()){
      return null
    }
    if(getUserByEmail(email).isDefined){
     return null
    }
    val connection = db.getConnection()
    val statement = connection.createStatement()
    val query = s"INSERT INTO USERS(EMAIL,PASSWORD,AGE,DESCRIPTION) VALUES ('$email','$password',NULL,NULL)"
    val resultset = statement.executeUpdate(query)
    return getUserByEmail(email);
  }
  
  def allUsers():List[User] = {
    var allUsers = List[User]();
    val connection = db.getConnection()
    val statement = connection.createStatement()
    val query = s"SELECT EMAIL as sqlemail, PASSWORD as sqlpassword, AGE as sqlage, DESCRIPTION as sqldescription FROM USERS"
    val resultset = statement.executeQuery(query)
    while(resultset.next){
      val email = resultset.getString("sqlemail");
       val password = resultset.getString("sqlpassword");
       val age = resultset.getInt("sqlage");
       val description = resultset.getString("sqldescription");
       allUsers = allUsers.::(User(email, password, age, description));
    }
    return allUsers;
  }
  
  
}