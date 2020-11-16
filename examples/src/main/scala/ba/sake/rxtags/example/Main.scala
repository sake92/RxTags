package ba.sake.rxtags.example

import org.scalajs.dom

object Main {

  def main(args: Array[String]): Unit = {

    val root = dom.document.getElementById("root")

    val content = Ex4.content().render

    root.appendChild(content)
  }

}
