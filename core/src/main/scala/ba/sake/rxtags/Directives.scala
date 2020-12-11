package ba.sake.rxtags

import org.scalajs.dom
import scalatags.JsDom.all.Modifier

trait Directive extends Modifier {
  def execute(elem: dom.Element): Unit

  override def applyTo(elem: dom.Element): Unit = {
    if (dom.document.contains(elem)) {
      execute(elem)
    }
  }
}

// TODO make'm pretty with := ???
trait Directives {

  case class focus(range: (Int, Int)) extends Directive {
    private val (start, end) = range

    def execute(elem: dom.Element): Unit = {
      val inputElem = elem.asInstanceOf[dom.html.Input]
      inputElem.focus()
      inputElem.setSelectionRange(start, end)
    }
  }
}
