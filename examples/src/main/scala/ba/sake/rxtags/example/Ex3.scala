package ba.sake.rxtags.example

import ba.sake.rxtags._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalatags.JsDom.all._

object Ex3 {

  val username$ = Var("")

  case class User(name: String)
  val users$ = Var(List.empty[User])

  username$.attach { u =>
    users$.transform(_.prepended(User(u)))
  }

  def content(): Frag =
    div(
      "Please enter your username:",
      input(onkeyup := updateUsername()),
      username$.map { u =>
        div(
          s"Username: $u",
          div("aaaaaaaaaaa")
        )
      }.asFrag,
      username$.map { u =>
        frag(b(">>>>>"), u, b("<<<<<<<"))
      }.asFrag
    )

  def updateUsername(): (dom.KeyboardEvent => Unit) =
    e => {
      val inputField = e.target.asInstanceOf[HTMLInputElement]
      //username$.set(inputField.value)
      username$.transform { _ =>
        inputField.value
      }
    }
}
