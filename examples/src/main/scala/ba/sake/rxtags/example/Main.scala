package ba.sake.rxtags.example

import ba.sake.rxtags.Var
import org.scalajs.dom
import scalatags.JsDom.all._

object Main {

  def main(args: Array[String]): Unit = {

    val root = dom.document.getElementById("root")
    val content = Ex3.content().render
    root.appendChild(content)

    /*
    val myVar = Var(123)

    def updateInput() = (e: dom.Event) => {
      myVar.set(myVar.get + 1)
    }

    val content = div(
      h4("Result:"),
      "Whatever",
      myVar.map(v => div("div:" + v)).asFrag,
      ul(
        li("something"),
        li(cls := myVar)("test")
      ),
      input(onkeyup := updateInput()),
      myVar.map(v => frag("frag:", v)).asFrag // NE RADIIIIIIII kad je frag()
    )*/

    /*
    val updateBtn = dom.document.getElementById("increment")
    updateBtn.addEventListener("click", (e: dom.MouseEvent) => {
      myVar.set(myVar.get + 1)
    })*/
  }
}
