package ba.sake.rxtags

import org.scalajs.dom
import org.scalajs.dom.raw.Node
import scalatags.JsDom.all._

/**
  * Adaptation from https://medium.com/@deathmood/how-to-write-your-own-virtual-dom-ee74acc13060
  */
private[rxtags] object VDOM {

  def updateElement(
      parent: Node,
      maybeNewFrag: Option[Frag],
      maybeOldFrag: Option[Frag],
      oldNodeIdx: Int,
      seqFrag: Boolean
  ): Int = {
    //println("updateElement", oldNodeIdx, parent, "new", maybeNewFrag, "old", maybeOldFrag)

    (maybeNewFrag, maybeOldFrag) match {
      case (Some(newFrag), None) =>
        val newElement = newFrag.render
        //println(s"appending '${newElement.innerText}' at $oldNodeIdx to '${parent.innerText}'")
        if (seqFrag) {
          val referenceElement = parent.childNodes(oldNodeIdx)
          parent.insertBefore(newElement, referenceElement)
        } else {
          parent.appendChild(newElement)
        }
      //println("appending done")
      case (None, _) =>
        //println(s"removing $oldNodeIdx")
        val removeElement = parent.childNodes(oldNodeIdx)
        parent.removeChild(removeElement)
      //println(s"removing done $oldNodeIdx")
      case (Some(newSF: SeqFrag[_]), Some(oldSF: SeqFrag[_])) => return updateElementsInSeqFrag(
          parent,
          oldNodeIdx,
          newSF.asInstanceOf[SeqFrag[Frag]],
          oldSF.asInstanceOf[SeqFrag[Frag]]
        )
      case (Some(newSF: bindNode[_]), Some(oldSF: bindNode[_])) =>
        // these are already rendered DOM Elements
        // TODO maybe do something smarter here..?
        //println("Handling bindNode", newSF)
        parent.replaceChild(
          newSF.render,
          parent.childNodes(oldNodeIdx)
        )
      //println("Done handling bindNode", newSF)
      case (Some(newFrag), Some(oldFrag)) =>
        if (didChange(newFrag, oldFrag)) {
          //println(s"replaceChild $oldNodeIdx", newFrag, oldFrag)
          parent.replaceChild(
            newFrag.render,
            parent.childNodes(oldNodeIdx)
          )
          //println(s"DONE replaceChild $oldNodeIdx", newFrag, oldFrag)
        } else { // if not changed, check children also
          // only "real" tags are diffable..
          handleHtmlTag(
            parent,
            oldNodeIdx,
            newFrag.asInstanceOf[HtmlTag],
            oldFrag.asInstanceOf[HtmlTag]
          )
        }
    }
    //println("DONE updateElement", oldNodeIdx, parent, "new", maybeNewFrag, "old", maybeOldFrag)
    1
  }

  def updateElementsInSeqFrag(
      parent: Node,
      oldNodeIdx: Int,
      newSF: SeqFrag[Frag],
      oldSF: SeqFrag[Frag]
  ): Int = {

    val newChildrenFrags = newSF.xs.map(x => newSF.ev(x)).filterNot(isEmptyStringFrag)
    val oldChildrenFrags = oldSF.xs.map(x => oldSF.ev(x)).filterNot(isEmptyStringFrag)

    //println("[SeqFrag] newChildrenFrags", newChildrenFrags)

    var len = newChildrenFrags.length max oldChildrenFrags.length
    var i = 0
    while (i < len) {
      val childCountBefore = parent.childNodes.length
      //println("[SeqFrag] Before", i)
      updateElement(
        parent, // parent is same here!!
        newChildrenFrags.lift(i),
        oldChildrenFrags.lift(i),
        i + oldNodeIdx,
        true
      )
      //println("[SeqFrag] After", i)
      val childCountAfter = parent.childNodes.length
      if (childCountAfter < childCountBefore) {
        // when child deleted do not increment i
        len -= 1
      } else {
        i += 1
      }
    }
    newChildrenFrags.length
  }

  private def handleHtmlTag(
      parent: Node,
      oldNodeIdx: Int,
      newTag: HtmlTag,
      oldTag: HtmlTag
  ): Unit = {

    def isAP(m: Modifier) = m.isInstanceOf[AttrPair] || m.isInstanceOf[SeqNode[_]]

    //println("handleHtmlTag", parent, oldNodeIdx, newTag, oldTag)

    val newNodeChildren = newTag.modifiers.flatten
    val oldNodeChildren = oldTag.modifiers.flatten
    val (newAttrPairMods, newChildrenFrags) = newNodeChildren.partition(isAP)
    val (oldAttrPairsMods, oldChildrenFrags) = oldNodeChildren.partition(isAP)

    val oldNode = parent.childNodes(oldNodeIdx)
    var i = 0

    // handle attributes
    locally {
      val oldElem = oldNode.asInstanceOf[dom.Element]
      oldAttrPairsMods.collect { case ap: AttrPair => oldElem.removeAttribute(ap.a.name) }
      newAttrPairMods.foreach(ap => ap.applyTo(oldElem))
    }
    //println("handleHtmlTag done attrs", parent, oldNodeIdx, newTag, oldTag)

    // handle children
    locally {
      //println("newChildrenFrags", newChildrenFrags, oldChildrenFrags)
      i = 0
      while (i < newChildrenFrags.length || i < oldChildrenFrags.length) {
        //println("Handling child ", i)
        updateElement(
          oldNode,
          newChildrenFrags.lift(i).map(_.asInstanceOf[Frag]),
          oldChildrenFrags.lift(i).map(_.asInstanceOf[Frag]),
          i,
          false
        )
        //println("DONE handling child ", i)
        i += 1
      }
    }
    //println("DONE handleHtmlTag", parent, oldNodeIdx, newTag, oldTag)
  }

  private def didChange(newFrag: Frag, oldFrag: Frag): Boolean =
    if (newFrag.getClass != oldFrag.getClass) true
    else (newFrag, oldFrag) match {
      case (newFrag: HtmlTag, oldFrag: HtmlTag) => newFrag.tag != oldFrag.tag
      case _                                    => true
    }

  private def isEmptyStringFrag(frag: Frag): Boolean =
    frag match {
      case sf: StringFrag => sf.v.trim.isEmpty
      case _              => false
    }
}
