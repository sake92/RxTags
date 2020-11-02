package ba.sake.rxtags.example

import scalatags.JsDom.all._

object Ex1 {

  val number = 123

  def content(): Frag =
    div(
      "This is an example",
      div(style := "color:red;")("Red text"),
      footer(cls := "dummy")(
        s"Hello, visitor number $number"
      )
    )
}
