package ba.sake.rxtags.example

import ba.sake.rxtags._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalatags.JsDom.all._

object Ex3 {

  val counter$ = Var(0)

  dom.window.setInterval(
    () => {
      println("tick")
      counter$.set(counter$.get + 1)
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

  /* "ADVANCED":
   `counter$.set(counter$.get + 1)`
      could be written as
   `counter$.transform(_ + 1)`
   */
}
