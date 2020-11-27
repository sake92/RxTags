package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags._

// RxTags variable
object Ex3 extends Example {

  val username$ = Var("")

  def content = div(
    "Please enter your username: ",
    input(onkeydown := updateUsername),
    username$.map { u =>
      s"Username: $u"
    }.asFrag
  )

  def updateUsername: (dom.KeyboardEvent => Unit) =
    e => {
      val inputField = e.target.asInstanceOf[dom.html.Input]
      username$.set(inputField.value)
    }
}
