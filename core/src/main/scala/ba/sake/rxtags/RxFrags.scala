package ba.sake.rxtags

import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._
import scalatags.jsdom
import reactify._

private[rxtags] trait RxFrags {

  // TODO Var[List[Frag]]
  // TODO Var[T] koji nije Frag.. :D

  implicit class ValFragOps[T <: Frag](private val rxFrag: Val[T]) {
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  implicit class VarFragOps[T <: Frag](private val rxFrag: Var[T]) {
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  implicit class ValStringOps[T](private val rxString: Val[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  implicit class VarStringOps[T](private val rxString: Var[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  private class ReactifiedFrag[T <: Frag](rxFrag: Stateful[T]) extends jsdom.Frag {
    private var maybeOldNode: Option[Frag] = None

    override def applyTo(parent: Element): Unit = {
      super.applyTo(parent)
      val currentCount = parent.childNodes.length - 1
      println("RxFraggg ", rxFrag, currentCount)
      rxFrag.attachAndFire { frag =>
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
