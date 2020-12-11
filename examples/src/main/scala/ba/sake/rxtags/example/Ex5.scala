package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags._

// RxTags variable
object Ex5 extends Example {

  val ticker$ = Var(0)

  dom.window.setInterval(
    () => { ticker$.set(_ + 1) },
    1000
  )

  def content = div(
    "Ticker: ",
    ticker$
  )

}
