package ba.sake.rxtags

import java.util.UUID

import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all.{Attr, AttrValue}
import reactify._

private[rxtags] trait RxAttrValues {

  implicit def val2AttrValue[T: AttrValue]: AttrValue[Val[T]] = rx2AttrValue[T, Val[T]]

  implicit def var2AttrValue[T: AttrValue]: AttrValue[Var[T]] = rx2AttrValue[T, Var[T]]

  private def rx2AttrValue[T: AttrValue, Rx <: Stateful[T]]: AttrValue[Rx] =
    new AttrValue[Rx] {

      // classes are handled specially..
      private var classes = Set.empty[String]

      override def apply(element: Element, attr: Attr, rxAttrValue: Rx): Unit =
        rxAttrValue.attachAndFire { newValue =>
          if (attr.name == "class") {
            handleClass(newValue, element)
          }
          implicitly[AttrValue[T]].apply(element, attr, newValue)
        }

      private def handleClass(newValue: T, element: Element): Unit = {
        val newValStr = newValue match {
          case opt: Option[Any] => opt.map(_.toString).getOrElse("")
          case other            => other.toString
        }
        val newClasses = newValStr.split(" ").map(_.trim).filterNot(_.isEmpty)
        classes ++= newClasses

        // remove all classes handled by this RX,
        // before maybe adding them again
        classes.foreach { cn =>
          if (!newClasses.contains(cn)) {
            element.classList.remove(cn)
          }
        }
      }
    }
}
