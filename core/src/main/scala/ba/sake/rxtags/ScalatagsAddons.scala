package ba.sake.rxtags

import org.scalajs.dom
import scalatags.JsDom.all.{Attr, AttrValue}
import scalatags.generic

private[rxtags] trait ScalatagsAddons {

  // 1. Not all attributes are reflected to properties, so we special-case them..
  //    https://stackoverflow.com/a/45474861/4496364
  // 2. If it's a reactive class, we just delegate to its handler,
  // in order to not break RxValue classes...
  implicit def optionAttrValue[T](implicit ev: AttrValue[T]): generic.AttrValue[dom.Element, Option[T]] =
    new AttrValue[Option[T]] {
      override def apply(t: dom.Element, a: Attr, v: Option[T]): Unit = {
        v match {
          case Some(value) =>
            ev.apply(t, a, value)
            ScalatagsAddons.applyAttrAndProp(t, a.name, v)
          case None =>
            if (a.name == "class") {
              ev.apply(t, a, "".asInstanceOf[T])
            } else {
              t.removeAttribute(a.name)
              ScalatagsAddons.applyAttrAndProp(t, a.name, v)
            }
        }
      }
    }
}

object ScalatagsAddons {
  // not all attributes are reflected to properties, so we special-case them..
  // https://stackoverflow.com/a/45474861/4496364
  def applyAttrAndProp[T](element: dom.Element, attrName: String, attrValue: Any): Unit = {
    val currentValue = attrValue match {
      case rx: Stateful[_] => rx.now
      case other           => other
    }
    val value = currentValue match {
      case opt: Option[_] => opt
      case other          => Some(other)
    }
    value match {
      case Some(v) => attrName match {
          case "value"   => element.asInstanceOf[dom.html.Input].value = v.toString
          case "checked" => element.asInstanceOf[dom.html.Input].checked = true
          case _         => // noop
        }
      case None => attrName match {
          case "value"   => element.asInstanceOf[dom.html.Input].value = ""
          case "checked" => element.asInstanceOf[dom.html.Input].checked = false
          case _         => // noop
        }
    }
  }
}
