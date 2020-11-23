package ba.sake.rxtags.example

import scalatags.JsDom.all._
import ba.sake.rxtags._

object Ex3 {

  val counter$ = Var(0)

  def content(): Frag =
    div(
      h1("Example 3"),
      counter$.map(c => h3(s"Reactive counter: $c")).asFrag,
      hr,
      button(onclick := add(-1))("-"),
      button(onclick := add(1))("+")
    )

  def add(incr: Int) =
    () => {
      counter$.set(c => c + incr)
    }
}
