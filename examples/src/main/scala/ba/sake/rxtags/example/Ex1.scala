package ba.sake.rxtags.example

import scalatags.JsDom.all._

object Ex1 {

  val number = 123

  def content(): Frag =
    div(
      h2("Example 1"),
      h4("Header example"),
      footer(cls := "bg-warning")(
        s"Hello, visitor number $number"
      )
    )
}
