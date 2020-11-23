package ba.sake.rxtags

import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.jsdom

private[rxtags] trait RxFrags {

  implicit class ValFragOps[T <: Frag](rxFrag: Val[T]) {
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class VarFragOps[T <: Frag](rxFrag: Var[T]) {
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  /* Strings */
  implicit class ValStringOps[T](rxString: Val[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  implicit class VarStringOps[T](rxString: Var[String]) {
    private val rxFrag = rxString.map(StringFrag)
    def asFrag: Frag = new RxFrag(rxFrag)
  }

  /* Seqs */
  implicit class ValSeqOps[CC <: Seq[Frag]](rxSeq: Val[CC]) {
    implicit val ev: Frag => Frag = identity
    private val rxFrag = rxSeq.map(s => SeqFrag(s))
    def asFrag: Frag = new RxFrag(rxFrag, true)
  }

  implicit class VarSeqOps[CC <: Seq[Frag]](rxSeq: Var[CC]) {
    implicit val ev: Frag => Frag = identity
    private val rxFrag = rxSeq.map(s => SeqFrag(s))
    def asFrag: Frag = new RxFrag(rxFrag, true)
  }

  /* SeqFrags are pretty complicated, so we track their elements counts dynamically */
  case class FragData(nodeIdx: Int, totalNodes: Int)

  // parent -> (fragId -> seqFragData)
  private var seqFragDatas = Map.empty[dom.Element, Map[Int, FragData]].withDefaultValue(Map.empty)

  class RxFrag[T <: Frag](rxFrag: Stateful[T], seqFrag: Boolean = false) extends jsdom.Frag {
    private var maybeOldFrag: Option[Frag] = None
    private var parent: dom.Element = _
    private var fragId: Int = _
    private var staticElems: Int = _
    private var nodeIdx: Int = 0

    override def applyTo(parent: dom.Element): Unit = {
      nodeIdx = parent.childNodes.length
      val firstRender = this.render
      parent.appendChild(firstRender)

      locally {
        val parentDatas = seqFragDatas(parent)
        this.fragId = parentDatas.keys.maxOption.map(_ + 1).getOrElse(0)
        this.staticElems = nodeIdx - seqFragDatas(parent).values.map(_.totalNodes).sum
        val totalNodes = rxFrag.now match {
          case sf: SeqFrag[_] => sf.xs.length
          case _: StringFrag  => 1
          case _: RawFrag     => 1
          case _              => firstRender.childNodes.length
        }
        val fragData = FragData(nodeIdx, totalNodes)
        val newParentDatas = parentDatas + (fragId -> fragData)
        seqFragDatas += parent -> newParentDatas
      }

      rxFrag.attachAndFire { frag =>
        // println("*" * 50)
        // println("Update", frag, nodeIdx)

        nodeIdx = seqFragDatas(parent).filter(_._1 < fragId).values.map(_.totalNodes).sum

        // update DOM
        val idx = nodeIdx + staticElems
        val totalNodes =
          if (seqFrag) VDOM.updateElementsInSeqFrag(
            parent,
            idx,
            frag.asInstanceOf[SeqFrag[Frag]],
            maybeOldFrag.get.asInstanceOf[SeqFrag[Frag]]
          )
          else VDOM.updateElement(parent, Option(frag), maybeOldFrag, idx, seqFrag)

        maybeOldFrag = Option(frag)

        // update count
        val parentDatas = seqFragDatas(parent)
        val fragData = parentDatas(fragId)
        val newFragData = fragData.copy(nodeIdx = nodeIdx, totalNodes = totalNodes)
        val newParentDatas = parentDatas + (fragId -> newFragData)
        seqFragDatas += parent -> newParentDatas
      }
    }

    override def render: dom.Node = {
      val initialFrag = rxFrag.now
      maybeOldFrag = Option(initialFrag)
      initialFrag.render
    }

    override def toString: String = {
      "RxFrag(" + maybeOldFrag.toString + ")"
    }
  }
}
