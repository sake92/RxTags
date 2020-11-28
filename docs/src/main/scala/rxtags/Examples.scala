package rxtags

import org.scalajs.dom
import scalatags.JsDom.all._
import ba.sake.rxtags.example._

object Examples {

  def main(args: Array[String]): Unit = {

    val examples = List(Ex1, Ex2, Ex3, Ex4, Ex5, Ex6)

    examples.zipWithIndex.foreach { case (ex, i) =>
      val num = i + 1
      val root = dom.document.getElementById(s"ex$num")
      if (root != null) {
        root.appendChild(
          div(ex.content).render
        )
      }
    }
  }
}
