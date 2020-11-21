package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags._

object Ex3 {

  val counter$ = Var(0)

  dom.window.setInterval(
    () => {
      println("tick")
      counter$.set(counter$.now + 1) // or counter$.set(_ + 1)
    },
    1000
  )

  def content(): Frag =
    div(
      h3("Reactive counter"),
      counter$.map { c =>
        s"Counter: $c"
      }.asFrag
    )
}
