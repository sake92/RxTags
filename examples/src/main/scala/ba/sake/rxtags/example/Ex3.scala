package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags._

// RxTags variable
object Ex3 extends Example {

  val name$ = Var("")

  def content = div(
    "Please enter your name: ",
    input(onkeyup := updateName),
    br,
    name$.map { name =>
      s"Your name: $name"
    }.asFrag
  )

  def updateName: (dom.KeyboardEvent => Unit) =
    e => {
      val inputField = e.target.asInstanceOf[dom.html.Input]
      name$.set(inputField.value)
    }
}
