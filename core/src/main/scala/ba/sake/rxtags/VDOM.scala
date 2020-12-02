package ba.sake.rxtags

import org.scalajs.dom
import org.scalajs.dom.raw.Node
import org.scalajs.dom.ext._
import scalajs.js
import scalatags.JsDom.all._

/**
  * Adaptation from https://medium.com/@deathmood/how-to-write-your-own-virtual-dom-ee74acc13060
  */
private[rxtags] object VDOM {

  def getId(node: dom.Node): String =
    node.asInstanceOf[js.Dynamic].scalaTag.id.toString

  def update(
      parent: dom.Node,
      maybeOldFragId: Option[String],
      maybeOldFrag: Option[Frag],
      maybeNewFrag: Option[Frag]
  ): Unit = {
    //println("update", inSeqFrag, maybeOldFragId, parent, "old", maybeOldFrag, "new", maybeNewFrag)

    (maybeOldFrag, maybeNewFrag) match {
      case (None, Some(newFrag)) => // append
        parent.appendChild(newFrag.render)
      case (_, None) => // remove
        val maybeExistingNode = parent.childNodes.toSeq.find { cn =>
          getId(cn) == maybeOldFragId.get
        }
        maybeExistingNode.foreach(parent.removeChild)
      case (Some(oldSF: SeqFrag[_]), Some(newSF: SeqFrag[_])) =>
        updateSeqFrag(
          maybeOldFragId.get,
          parent,
          oldSF.asInstanceOf[SeqFrag[Frag]],
          newSF.asInstanceOf[SeqFrag[Frag]]
        )
      case (Some(oldSF: bindNode[_]), Some(newSF: bindNode[_])) =>
        // these are already rendered DOM Elements
        parent.replaceChild(newSF.render, oldSF.render)
      case (Some(oldFrag), Some(newFrag)) =>
        val maybeExistingNode = parent.childNodes.toSeq.find { cn =>
          getId(cn) == maybeOldFragId.get
        }
        maybeExistingNode match {
          case None =>
            println(s"Wooooooops, no node $maybeOldFragId", oldFrag, newFrag)
          case Some(existingNode) =>
            if (didChange(newFrag, oldFrag)) {
              parent.replaceChild(newFrag.render, existingNode)
            } else { // do the diffing
              handleHtmlTag(
                existingNode,
                newFrag.asInstanceOf[HtmlTag],
                oldFrag.asInstanceOf[HtmlTag]
              )
            }
        }
    }
    // println("DONE update", inSeqFrag, maybeOldFragId, maybeNewFragId, parent, "old", maybeOldFrag, "new", maybeNewFrag)
  }

  def updateSeqFrag(
      fragId: String,
      parent: Node,
      oldSF: SeqFrag[Frag],
      newSF: SeqFrag[Frag]
  ): Unit = {

    val oldChildrenFrags = oldSF.xs.map(x => oldSF.ev(x)).filterNot(isEmptyStringFrag)
    val newChildrenFrags = newSF.xs.map(x => newSF.ev(x)).filterNot(isEmptyStringFrag)

    val len = newChildrenFrags.length max oldChildrenFrags.length
    var i = 0
    while (i < len) {
      val oldChildFragId = oldChildrenFrags.lift(i).map(fr => getId(fr.render))
      update(
        parent, // parent is same here!!
        oldChildFragId,
        oldChildrenFrags.lift(i),
        newChildrenFrags.lift(i)
      )
      i += 1
    }
  }

  private def handleHtmlTag(
      existingNode: Node,
      newTag: HtmlTag,
      oldTag: HtmlTag
  ): Unit = {
    // always update the ID, prepare for next diffing
    existingNode.asInstanceOf[js.Dynamic].scalaTag.id = getId(newTag.render)

    //println("handleHtmlTag", parent, oldNodeIdx, newTag, oldTag)

    val (newAttrPairMods, newChildrenFrags) = newTag.modifiers.flatten.flatMap {
      case sn: SeqNode[_] => sn.xs.map(x => sn.ev(x))
      case other          => Seq(other)
    }.partition(_.isInstanceOf[AttrPair])

    val (oldAttrPairsMods, oldChildrenFrags) = oldTag.modifiers.flatten.flatMap {
      case sn: SeqNode[_] => sn.xs.map(x => sn.ev(x))
      case other          => Seq(other)
    }.partition(_.isInstanceOf[AttrPair])

    var i = 0

    // handle attributes
    locally {
      val oldElem = existingNode.asInstanceOf[dom.Element]
      oldAttrPairsMods.map(_.asInstanceOf[AttrPair]).foreach { ap =>
        oldElem.removeAttribute(ap.a.name)
        ScalatagsAddons.applyAttrAndProp(oldElem, ap.a.name, None)
      }
      newAttrPairMods.map(_.asInstanceOf[AttrPair]).foreach { ap =>
        ap.applyTo(oldElem)
        ScalatagsAddons.applyAttrAndProp(oldElem, ap.a.name, ap.v)
      }
    }
    //println("handleHtmlTag done attrs", parent, oldNodeIdx, newTag, oldTag)

    // handle children
    locally {
      //println("newChildrenFrags", newChildrenFrags, oldChildrenFrags)
      i = 0
      while (i < newChildrenFrags.length || i < oldChildrenFrags.length) {
        //println("Handling child ", i)
        val maybeChildFragId = oldChildrenFrags.lift(i).map(_.asInstanceOf[Frag].render).map(getId)
        update(
          existingNode,
          maybeChildFragId,
          oldChildrenFrags.lift(i).map(_.asInstanceOf[Frag]),
          newChildrenFrags.lift(i).map(_.asInstanceOf[Frag])
        )
        //println("DONE handling child ", i)
        i += 1
      }
    }
    //println("DONE handleHtmlTag", parent, oldNodeIdx, newTag, oldTag)
  }

  private def didChange(newFrag: Frag, oldFrag: Frag): Boolean =
    (newFrag, oldFrag) match {
      case (nf: HtmlTag, of: HtmlTag) =>
        nf.tag != of.tag
      case _ => true
    }

  private def isEmptyStringFrag(frag: Frag): Boolean =
    frag match {
      case sf: StringFrag => sf.v.trim.isEmpty
      case _              => false
    }

}
