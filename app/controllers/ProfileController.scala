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


class ProfileController @Inject() (cc: ControllerComponents, tweetManager : TweetManager) 
extends AbstractController(cc) 
with play.api.i18n.I18nSupport{
  
  
  
}