package ba.sake.rxtags

import org.scalajs.dom.{Element, Node}
import scalatags.JsDom.all._
import scalatags.jsdom
import reactify._

private[rxtags] trait RxFrags {

  // TODO Var[List[Frag]]
  // TODO Var[T] koji nije Frag.. :D

  implicit class ValFragOps[T <: Frag](rxFrag: Val[T]) {
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  implicit class VarFragOps[T <: Frag](rxFrag: Var[T]) {
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  /* Strings */
  implicit class ValStringOps[T](rxString: Val[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  implicit class VarStringOps[T](rxString: Var[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  /* Seqs */
  /*implicit class ValSeqOps[T, CC <: Seq[T]](rxSeq: Val[CC])(implicit ev: T => Frag) {
    private val rxFrag = rxSeq.map(seq => SeqFrag(seq))
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }*/

  implicit class VarSeqOps[CC <: Seq[Frag]](rxSeq: Var[CC]) {
    implicit val ev: Frag => Frag = identity
    private val rxFrag = rxSeq.map(seq => SeqFrag(seq))
    def asFrag: Frag = new ReactifiedFrag(rxFrag)
  }

  private class ReactifiedFrag[T <: Frag](rxFrag: Stateful[T]) extends jsdom.Frag {
    private var maybeOldFrag: Option[Frag] = None
    private var parent: Element = _
    private var fragIdx: Int = 0

    override def applyTo(parent: Element): Unit = {
      this.parent = parent
      super.applyTo(parent)

      rxFrag.attach { frag =>
        //println("Update", frag, fragIdx)
        VDOM.updateElement(parent, Option(frag), maybeOldFrag, fragIdx)
        maybeOldFrag = Option(frag)
      }
    }

    override def render: Node = {
      fragIdx = this.parent.childNodes.length // needs to happen before
      val initialFrag = rxFrag.get
      maybeOldFrag = Option(initialFrag)
      initialFrag.render
    }
  }
}
