package ba.sake.rxtags

import java.util.UUID

import org.scalajs.dom
import scalatags.JsDom.all._

private[rxtags] trait RxFrags {

  implicit class StatefulFragOps[T <: Frag](rxFrag: Stateful[T]) {
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class StatefulStringOps[T](rxString: Stateful[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class StatefulNumericOps[T: Numeric](rxNum: Stateful[T]) {
    private val rxFrag = rxNum.map(n => StringFrag(n.toString))
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class StatefulSeqFragOps[CC <: Seq[Frag]](rxSeq: Stateful[CC]) {
    implicit val ev: Frag => Frag = identity
    private val rxFrag = rxSeq.map(s => SeqFrag(s))
    def asFrag: Frag = new RxFrag(rxFrag)
  }
}

private[rxtags] class RxFrag[T <: Frag](val rxFrag: Stateful[T]) extends Frag {

  private var oldFrag: T = rxFrag.now
  private val node: dom.Node = oldFrag.render
  private var fragId: String = VDOM.getId(node)

  private var parent: dom.Element = _

  override def render: dom.Node = node

  override def applyTo(newParent: dom.Element): Unit = {
    parent = newParent
    parent.appendChild(node)
    rxFrag.attachAndFire { _ =>
      update()
    }
  }

  def update(): Unit = {
    val newFrag = rxFrag.now
    VDOM.update(parent, Option(fragId), Option(oldFrag), Option(newFrag))
    oldFrag = newFrag

    // always use new frag's ID, prepare for next DIFFing
    fragId = VDOM.getId(newFrag.render)
    VDOM.setId(node, fragId)
  }

  override def toString: String = {
    "RxFrag(" + oldFrag.toString + ")"
  }
}
