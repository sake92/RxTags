package ba.sake.rxtags

import org.scalajs.dom
import scalatags.JsDom.all.{Attr, AttrValue}

private[rxtags] trait RxAttrValues {

  implicit def val2AttrValue[T: AttrValue]: AttrValue[Val[T]] = new RxAttrValue[T, Val[T]]

  implicit def var2AttrValue[T: AttrValue]: AttrValue[Var[T]] = new RxAttrValue[T, Var[T]]
}

class RxAttrValue[T: AttrValue, Rx <: Stateful[T]] extends AttrValue[Rx] {

  // classes are handled specially..
  private var classes = Set.empty[String]

  override def apply(element: dom.Element, attr: Attr, rxAttrValue: Rx): Unit = {
    rxAttrValue.attachAndFire { newValue =>
      //println("apply ", newValue, VDOM.getId(element))
      implicitly[AttrValue[T]].apply(element, attr, newValue)
      attr.name match {
        case "class"  => handleClass(newValue, element)
        case attrName => ScalatagsAddons.applyAttrAndProp(element, attrName, newValue)
      }
    }
  }

  // if we have 2 Rx classes, both these handle different class names,
  // so we only add/remove classes for *that particular RX* !
  def handleClass(newValue: Any, element: dom.Element): Unit = {
    val newValStr = newValue match {
      case opt: Option[Any] => opt.map(_.toString).getOrElse("")
      case other            => other.toString
    }
    val newClasses = newValStr.split(" ").map(_.trim).filterNot(_.isEmpty)
    classes ++= newClasses

    // remove all classes handled by this RX,
    // before maybe-adding them again
    classes.foreach { cn =>
      if (!newClasses.contains(cn)) {
        element.classList.remove(cn)
      }
    }
  }
}
