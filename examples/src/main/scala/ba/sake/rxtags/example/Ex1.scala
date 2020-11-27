package ba.sake.rxtags.example

import scalatags.JsDom.all._

// scalatags
object Ex1 extends Example {

  val number = 123

  def content = div(
    h4("Scalatags example"),
    footer(cls := "whatever")(
      s"Hello, visitor number $number"
    )
  )
}
