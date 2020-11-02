package ba.sake.rxtags

import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._
import reactify._

private[rxtags] trait RxAttrValues {

  implicit def val2AttrValue[T: AttrValue]: AttrValue[Val[T]] = rx2AttrValue[T, Val[T]]

  implicit def var2AttrValue[T: AttrValue]: AttrValue[Var[T]] = rx2AttrValue[T, Var[T]]

  private def rx2AttrValue[T: AttrValue, Rx <: Stateful[T]]: AttrValue[Rx] =
    new AttrValue[Rx] {

      override def apply(element: Element, attr: Attr, rxAttrValue: Rx): Unit =
        rxAttrValue.attachAndFire { newValue =>
          implicitly[AttrValue[T]].apply(element, attr, newValue)
        }
    }
}
