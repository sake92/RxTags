package ba.sake.rxtags

import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._
import reactify._

private[rxtags] trait RxStyleValues {

  implicit def val2StyleValue[T: StyleValue]: StyleValue[Val[T]] = rx2StyleValue[T, Val[T]]

  implicit def var2StyleValue[T: StyleValue]: StyleValue[Var[T]] = rx2StyleValue[T, Var[T]]

  private def rx2StyleValue[T: StyleValue, Rx <: Stateful[T]]: StyleValue[Rx] =
    new StyleValue[Rx] {

      override def apply(element: Element, style: Style, rxStyleValue: Rx): Unit =
        rxStyleValue.attachAndFire { newValue =>
          implicitly[StyleValue[T]].apply(element, style, newValue)
        }
    }
}
