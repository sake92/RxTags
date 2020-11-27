package ba.sake.rxtags.example

import scalatags.JsDom.all._
import ba.sake.rxtags._

// RxTags variable
object Ex4 extends Example {

  val counter$ = Var(0)

  def content = div(
    counter$.map(c => h4(s"Reactive counter: $c")).asFrag,
    button(onclick := add(-1), cls := "btn")("-"),
    button(onclick := add(1), cls := "btn")("+")
  )

  def add(incr: Int) =
    () => {
      counter$.set(c => c + incr)
    }

}
