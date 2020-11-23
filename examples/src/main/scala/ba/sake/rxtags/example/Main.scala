package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._

object Main {

  def main(args: Array[String]): Unit = {

    val root = dom.document.getElementById("root")

    val content = div(
      Ex1.content().render,
      hr,
      hr,
      Ex2.content().render,
      hr,
      hr,
      Ex3.content().render,
      hr,
      hr,
      Ex4.content().render,
      hr,
      hr,
      Ex5.content().render,
      hr,
      hr
    ).render

    root.appendChild(content)
  }
}
