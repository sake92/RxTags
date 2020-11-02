package ba.sake.rxtags

import org.scalajs.dom
import org.scalajs.dom.raw.Node
import scalatags.JsDom.all._

/**
  * Stolen from https://medium.com/@deathmood/how-to-write-your-own-virtual-dom-ee74acc13060
  */
object VDOM {

  def createElement(content: Frag): Node = content.render

  def updateElement(
      parent: Node,
      maybeNewFrag: Option[Frag],
      maybeOldFrag: Option[Frag],
      oldNodeIdx: Int
  ): Unit = {
    //println("updateElement", parent, maybeNewFrag, maybeOldFrag, oldNodeIdx)

    def handleChildren(newTag: HtmlTag, oldTag: HtmlTag): Unit = {
      val newNodeChildren = newTag.modifiers.flatten
      val oldNodeChildren = oldTag.modifiers.flatten
      val (newAttrPairs, newChildrenFrags) = newNodeChildren.partition(_.isInstanceOf[AttrPair])
      val (_, oldChildrenFrags) = oldNodeChildren.partition(_.isInstanceOf[AttrPair])

      var i = 0
      while (i < newChildrenFrags.length || i < oldChildrenFrags.length) {
        updateElement(
          parent.childNodes(oldNodeIdx),
          newChildrenFrags.lift(i).map(_.asInstanceOf[Frag]),
          oldChildrenFrags.lift(i).map(_.asInstanceOf[Frag]),
          i
        )
        i += 1
      }

      // handle new attributes of current node
      newAttrPairs.foreach { ap =>
        ap.applyTo(parent.childNodes(oldNodeIdx).asInstanceOf[dom.Element])
      }
    }

    def handleSeqFrags(newSF: SeqFrag[Frag], oldSF: SeqFrag[Frag]): Unit = {

      val newNodeChildren = newSF.xs.map(x => newSF.ev(x)).filterNot(isEmptyStringFrag)
      val oldNodeChildren = oldSF.xs.map(x => oldSF.ev(x)).filterNot(isEmptyStringFrag)

      var i = 0
      while (i < newNodeChildren.length || i < oldNodeChildren.length) {
        updateElement(
          parent, // parent is same here!
          newNodeChildren.lift(i),
          oldNodeChildren.lift(i),
          oldNodeIdx + i
        )
        i += 1
      }
    }

    (maybeNewFrag, maybeOldFrag) match {
      case (Some(newFrag), None) =>
        val newElement = createElement(newFrag)
        parent.appendChild(newElement)
      case (None, _) =>
        val removeElement = parent.childNodes(oldNodeIdx)
        parent.removeChild(removeElement)
      case (Some(newFrag), Some(oldFrag)) =>
        if (didChange(newFrag, oldFrag)) {
          parent.replaceChild(
            createElement(newFrag),
            parent.childNodes(oldNodeIdx)
          )
        } else { // if not changed, check children also
          (newFrag, oldFrag) match {
            case (newTag: HtmlTag, oldTag: HtmlTag)           => handleChildren(newTag, oldTag)
            case (newSF: SeqFrag[Frag], oldSF: SeqFrag[Frag]) => handleSeqFrags(newSF, oldSF)
            case _                                            =>
          }
        }
    }
  }

  private def didChange(newFrag: Frag, oldFrag: Frag): Boolean =
    if (newFrag.getClass != oldFrag.getClass) true
    else newFrag match {
      case _: StringFrag => true
      case _: RawFrag    => true
      case _: SeqFrag[_] => false
      case _ =>
        val node1 = newFrag.asInstanceOf[HtmlTag]
        val node2 = oldFrag.asInstanceOf[HtmlTag]
        node1.tag != node2.tag
    }

  private def isEmptyStringFrag(frag: Frag): Boolean =
    frag match {
      case sf: StringFrag => sf.v.trim.isEmpty
      case _              => false
    }
}
