package ba.sake.rxtags.example

import org.scalajs.dom
import scalatags.JsDom.all._

trait Example {
  def content: Frag
}

object Main {

  def main(args: Array[String]): Unit = {

    val root = dom.document.getElementById("root")

    val examples = List(Ex1, Ex2, Ex3, Ex4, Ex5, Ex6)

    val content = div(
      examples.zipWithIndex.map { case (ex, i) =>
        div(
          h2(s"Example ${i + 1}"),
          ex.content,
          hr
        )
      }
    )

    root.appendChild(content.render)
  }
}
