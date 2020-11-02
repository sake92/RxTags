package ba.sake.rxtags.example

import ba.sake.rxtags._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalatags.JsDom.all._

object Ex3 {

  val username$ = Var("")

  def content(): Frag =
    div(
      "Please enter your username:",
      input(onkeyup := updateUsername()),
      username$.map { u =>
        s"You entered DIV: $u"
      }.asFrag
    )

  def updateUsername(): (dom.KeyboardEvent => Unit) =
    e => {
      val inputField = e.target.asInstanceOf[HTMLInputElement]
      username$.set(inputField.value)
    }
}
