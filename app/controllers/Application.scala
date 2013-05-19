package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Action
import play.api.libs.Crypto

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  import play.api.data._
  import play.api.data.Forms._

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text))

  def login = Action { implicit request =>
    val (username, password) = loginForm.bindFromRequest().get
    val cookies =
      if (username == password) List(Cookie("authentication", Crypto.encryptAES("success")))
      else List.empty

    Ok(views.html.index("Your new application is ready.")).withCookies(cookies: _*)
  }

  def protectedAt(path: String, file: String) = Action { request =>
    request.cookies.get("authentication") match {
      case None => Redirect(routes.Application.index)
      case Some(cookie) =>
        if (Crypto.decryptAES(cookie.value) == "success")
          controllers.Assets.at(path, file)(request)
        else
          Redirect(routes.Application.index)
    }
  }

}