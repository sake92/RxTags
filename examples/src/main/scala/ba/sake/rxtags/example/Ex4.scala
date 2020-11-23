package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags._

object Ex4 {

  val username$ = Var("")

  def content(): Frag =
    div(
      h1("Example 4"),
      "Please enter your username: ",
      input(onkeyup := updateUsername()),
      br,
      username$.map { u =>
        s"Username: $u"
      }.asFrag
    )

  def updateUsername(): (dom.KeyboardEvent => Unit) =
    e => {
      val inputField = e.target.asInstanceOf[dom.html.Input]
      username$.set(inputField.value)
    }
}
