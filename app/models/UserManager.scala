package models


import javax.inject._
import play.api.Logger
import scala.collection.immutable.HashMap
import play.api.db._
import java.sql.ResultSet

case class User (email: String, password: String, age: Option[Int] = Option.empty, description: Option[String] = Option.empty)

@Singleton
class UserManager @Inject()(db: Database){
   implicit def toOption[E](value : E) = Some(value)
   
  // var users = List[User](User("email1@gmx.de","pw1",23,"adsfdescription"),User("email2@gmx.de","pw2"),User("email3@gmx.de","pw3"))
   
  def loginUser(email: String, password:String) : Option[User] = {
     val resultset = runMyQuery(s"SELECT EMAIL as sqlemail, PASSWORD as sqlpassword, AGE as sqlage, DESCRIPTION as sqldescription FROM USERS WHERE USERS.EMAIL='$email' and USERS.PASSWORD='$password'")
     //find out the rowcount
     resultset.last();
     if(resultset.getRow() <1){
       return null;
     }
     else if(resultset.getRow()>1){
       Logger.debug("apparently email is not primary key in database, an email request returned more than one result.")
       return null;
     }
     else{
       val email = resultset.getString("sqlemail");
       val password = resultset.getString("sqlpassword");
       val age = resultset.getInt("sqlage");
       val description = resultset.getString("sqldescription");
       return User(email, password, age, description);
     }
  }
 
   def getUserByEmail(email:String) : Option[User] = {
     val resultset = runMyQuery(s"SELECT EMAIL as sqlemail, PASSWORD as sqlpassword, AGE as sqlage, DESCRIPTION as sqldescription FROM USERS WHERE USERS.EMAIL='$email'")
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
    val resultset = runMyUpdate(s"INSERT INTO USERS(EMAIL,PASSWORD,AGE,DESCRIPTION) VALUES ('$email','$password',NULL,NULL)")
    return getUserByEmail(email);
  }
  
  def allUsers():List[User] = {
    var allUsers = List[User]();
    val resultset = runMyQuery(s"SELECT EMAIL as sqlemail, PASSWORD as sqlpassword, AGE as sqlage, DESCRIPTION as sqldescription FROM USERS")
    while(resultset.next){
      val email = resultset.getString("sqlemail");
      val password = resultset.getString("sqlpassword");
      val age = resultset.getInt("sqlage");
      val description = resultset.getString("sqldescription");
      allUsers = allUsers.::(User(email, password, age, description));
    }
    return allUsers;
  }
  
  var connection = db.getConnection()
  def runMyQuery(query: String) : ResultSet = {
    val statement = connection.createStatement()
    return statement.executeQuery(query)
  }
   def runMyUpdate(query: String){
    val statement = connection.createStatement()
    statement.executeUpdate(query)
  }
  
  
}