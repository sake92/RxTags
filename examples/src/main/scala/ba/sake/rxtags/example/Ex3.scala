package ba.sake.rxtags.example

import scalatags.JsDom.all._
import ba.sake.rxtags._

object Ex3 {

  val counter$ = Var(0)

  def content(): Frag =
    div(
      h2("Example 3"),
      counter$.map(c => h4(s"Reactive counter: $c")).asFrag,
      hr,
      button(onclick := add(-1), cls := "btn b-warning")("-"),
      button(onclick := add(1), cls := "btn b-accent")("+")
    )

  def add(incr: Int) =
    () => {
      counter$.set(c => c + incr)
    }
}
