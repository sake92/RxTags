package ba.sake.rxtags

import scala.language.implicitConversions
import org.scalajs.dom
import scalatags.JsDom.all._

private[rxtags] trait RxFrags {

  implicit def statefulRxFrag[T <: Frag](rxFrag: Stateful[T]): Frag =
    new RxFrag(rxFrag)

  implicit def statefulRxString(rxString: Stateful[String]): Frag = {
    val rxFrag = rxString.map(StringFrag)
    new RxFrag(rxFrag)
  }

  implicit def statefulRxNumeric[T: Numeric](rxNum: Stateful[T]): Frag = {
    val rxFrag = rxNum.map(n => StringFrag(n.toString))
    new RxFrag(rxFrag)
  }

  implicit def statefulRxSeq[CC <: Seq[Frag]](rxSeq: Stateful[CC]): Frag = {
    implicit val ev: Frag => Frag = identity
    val rxFrag = rxSeq.map(s => SeqFrag(s))
    new RxFrag(rxFrag)
  }
}

private[rxtags] class RxFrag[T <: Frag](val rxFrag: Stateful[T]) extends Frag {

  private var oldFrag: T = rxFrag.now
  private val node: dom.Node = oldFrag.render

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
    VDOM.update(parent, Option(oldFrag), Option(newFrag))
    oldFrag = newFrag

    // always use new frag's ID, prepare for next DIFFing
    val newFragId = VDOM.getId(newFrag.render)
    VDOM.setId(node, newFragId)
  }

  override def toString: String = {
    "RxFrag(" + oldFrag.toString + ")"
  }
}
