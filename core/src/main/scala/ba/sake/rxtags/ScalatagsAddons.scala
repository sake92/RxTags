package ba.sake.rxtags

import org.scalajs.dom
import scalatags.JsDom.all.{Attr, AttrValue}
import scalatags.generic

trait ScalatagsAddons {

  // 1. it's not enough to add/remove "checked", we need to remove the propery also..
  // 2. If it's a reactive class, we just delegate to its handler,
  // in order to not break RxValue classes...
  implicit def optionAttrValue[T](implicit ev: AttrValue[T]): generic.AttrValue[dom.Element, Option[T]] =
    new AttrValue[Option[T]] {
      override def apply(t: dom.Element, a: Attr, v: Option[T]): Unit = {
        v match {
          case Some(value) =>
            ev.apply(t, a, value)
            if (a.name == "checked") {
              t.asInstanceOf[dom.raw.HTMLInputElement].checked = true
            }
          case None =>
            if (a.name == "class") {
              ev.apply(t, a, "".asInstanceOf[T])
            } else {
              t.removeAttribute(a.name)
              if (a.name == "checked") {
                t.asInstanceOf[dom.raw.HTMLInputElement].checked = false
              }
            }
        }
      }
    }
}
