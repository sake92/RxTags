package ba.sake.rxtags

import org.scalajs.dom.{Element, Node}
import reactify._
import scalatags.JsDom.all._
import scalatags.jsdom

object rxtags extends RxFrags with RxAttrValues

trait Rx {

}

trait RxFrags {

  implicit class ValOps[T <: Frag](private val rxFrag: Val[T]) {
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  implicit class VarOps[T <: Frag](private val rxFrag: Var[T]) {
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  private class ReactifiedFrag[T <: Frag](rxFrag: Stateful[T]) extends jsdom.Frag {
    private var maybeOldNode: Option[Frag] = None

    override def applyTo(parent: Element): Unit = {
      super.applyTo(parent)
      val currentCount = parent.childElementCount
      rxFrag.attach { frag =>
        VDOM.updateElement(parent, Option(frag), maybeOldNode, currentCount)
        maybeOldNode = Option(frag)
      }
    }

    override def render: Node = {
      val initialFrag = rxFrag.get
      maybeOldNode = Option(initialFrag)
      initialFrag.render
    }
  }
}

trait RxAttrValues {

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
