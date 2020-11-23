package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags._

object Ex5 {

  val ticker$ = Var(0)

  dom.window.setInterval(
    () => {
      println("tick")
      ticker$.set(_ + 1)
    },
    1000
  )

  def content(): Frag =
    div(
      h2("Example 5"),
      ticker$.map { c =>
        s"Ticker: $c"
      }.asFrag
    )
}
