package ba.sake.rxtags.example

import scalatags.JsDom.all._

object Ex1 {

  val number = 123

  def content(): Frag =
    div(
      h3(cls := "active")(
        "This is an example"
      ),
      footer(style := "color:red;")(
        s"Hello, visitor number $number"
      )
    )
}
