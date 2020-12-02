package ba.sake.rxtags

import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.jsdom
import scalajs.js

private[rxtags] trait RxFrags {

  implicit class StatefulFragOps[T <: Frag](rxFrag: Stateful[T]) {
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class StatefulStringOps[T](rxString: Stateful[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class StatefulSeqFragOps[CC <: Seq[Frag]](rxSeq: Stateful[CC]) {
    implicit val ev: Frag => Frag = identity
    private val rxFrag = rxSeq.map(s => SeqFrag(s))
    def asFrag: Frag = new RxFrag(rxFrag, true)
  }

  class RxFrag[T <: Frag](rxFrag: Stateful[T], seqFrag: Boolean = false) extends jsdom.Frag {

    private var oldFrag = rxFrag.now
    private val node = oldFrag.render
    private var fragId = VDOM.getId(node)

    override def render: dom.Node = node

    override def applyTo(parent: dom.Element): Unit = {
      parent.appendChild(node)

      rxFrag.attach { newFrag =>
        // println("*" * 50)
        // println("Update", fragId, seqFrag, oldFrag, newFrag)

        VDOM.update(parent, Option(fragId), Option(oldFrag), Option(newFrag))

        oldFrag = newFrag
        // always use new frag's ID, prepare for next DIFFing
        fragId = VDOM.getId(newFrag.render)
        node.asInstanceOf[js.Dynamic].scalaTag.id = fragId
      }
    }

    override def toString: String = {
      "RxFrag(" + oldFrag.toString + ")"
    }
  }
}
