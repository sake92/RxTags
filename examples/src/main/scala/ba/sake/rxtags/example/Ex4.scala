package ba.sake.rxtags.example

import scalatags.JsDom.all._
import ba.sake.rxtags._

// RxTags variable
object Ex4 extends Example {

  val counter$ = Var(0)

  def content = div(
    counter$.map(c => h4(s"Reactive counter: $c")),
    button(onclick := add(-1))("-"),
    button(onclick := add(1))("+")
  )

  def add(incr: Int) =
    () => {
      counter$.set(c => c + incr)
    }

}
