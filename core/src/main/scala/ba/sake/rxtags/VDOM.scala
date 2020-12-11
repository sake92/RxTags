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

  def setId(node: dom.Node, id: String): Unit =
    node.asInstanceOf[js.Dynamic].scalaTag.id = id

  def hasId(node: dom.Node): Boolean =
    !js.isUndefined(node.asInstanceOf[js.Dynamic].scalaTag)

  def update(
      parent: dom.Node,
      maybeOldFrag: Option[Frag],
      maybeNewFrag: Option[Frag]
  ): Unit = {
    //  println("update", maybeOldFragId, parent, getId(parent), "old", maybeOldFrag, "new", maybeNewFrag)
    val maybeOldFragId = maybeOldFrag.map(fr => getId(fr.render))

    (maybeOldFrag, maybeNewFrag) match {
      case (None, Some(newFrag)) => // append
        parent.appendChild(newFrag.render)
      case (_, None) => // remove
        val maybeExistingNode = parent.childNodes.toSeq.find { cn =>
          hasId(cn) && getId(cn) == maybeOldFragId.get
        }
        maybeExistingNode.foreach(parent.removeChild)
      case (Some(oldSF: SeqFrag[_]), Some(newSF: SeqFrag[_])) =>
        updateSeqFrag(
          parent,
          oldSF.asInstanceOf[SeqFrag[Frag]],
          newSF.asInstanceOf[SeqFrag[Frag]]
        )
      case (Some(oldSF: bindNode[_]), Some(newSF: bindNode[_])) =>
        // these are already rendered DOM Elements
        parent.replaceChild(newSF.render, oldSF.render)
      case (Some(oldRxFrag: RxFrag[Frag]), Some(newRxFrag: RxFrag[Frag])) =>
        oldRxFrag.update()
      case (Some(oldFrag), Some(newFrag)) =>
        val maybeExistingNode = parent.childNodes.toSeq.find { cn =>
          hasId(cn) && getId(cn) == maybeOldFragId.get
        }
        maybeExistingNode match {
          case None =>
            println(s"Wooooooops, no node $maybeOldFragId", oldFrag, newFrag)
          case Some(existingNode) =>
            val (didChg, fragType) = didChange(newFrag, oldFrag)
            if (didChg) {
              parent.replaceChild(newFrag.render, existingNode)
            } else if (fragType == "StringFrag") {
              setId(existingNode, getId(newFrag.render))
            } else { // do the diffing
              handleHtmlTag(
                existingNode.asInstanceOf[dom.Element],
                oldFrag.asInstanceOf[HtmlTag],
                newFrag.asInstanceOf[HtmlTag]
              )
            }
        }
    }
    // println("DONE update", inSeqFrag, maybeOldFragId, maybeNewFragId, parent, "old", maybeOldFrag, "new", maybeNewFrag)
  }

  def updateSeqFrag(
      parent: Node,
      oldSF: SeqFrag[Frag],
      newSF: SeqFrag[Frag]
  ): Unit = {

    val oldChildrenFrags = oldSF.xs.map(x => oldSF.ev(x)).filterNot(isEmptyStringFrag)
    val newChildrenFrags = newSF.xs.map(x => newSF.ev(x)).filterNot(isEmptyStringFrag)

    val len = newChildrenFrags.length max oldChildrenFrags.length
    var i = 0
    while (i < len) {
      update(
        parent, // parent is same here!!
        oldChildrenFrags.lift(i),
        newChildrenFrags.lift(i)
      )
      i += 1
    }
  }
  private def handleHtmlTag(
      existingElement: dom.Element,
      oldTag: HtmlTag,
      newTag: HtmlTag
  ): Unit = {
    //println("handleHtmlTag", existingElement, newTag, oldTag)

    // always update the ID, prepare for next diffing
    setId(existingElement, getId(newTag.render))

    val oldMods = oldTag.modifiers.flatten.flatMap {
      case sn: SeqNode[_] => sn.xs.map(x => sn.ev(x))
      case other          => Seq(other)
    }
    val oldChildrenFrags = oldMods.filter(_.isInstanceOf[Frag]).map(_.asInstanceOf[Frag])
    val oldAttrPairs = oldMods.filter(_.isInstanceOf[AttrPair]).map(_.asInstanceOf[AttrPair])
    val oldDirectives = oldMods.filter(_.isInstanceOf[Directive]).map(_.asInstanceOf[Directive])

    val newMods = newTag.modifiers.flatten.flatMap {
      case sn: SeqNode[_] => sn.xs.map(x => sn.ev(x))
      case other          => Seq(other)
    }
    val newChildrenFrags = newMods.filter(_.isInstanceOf[Frag]).map(_.asInstanceOf[Frag])
    val newAttrPairs = newMods.filter(_.isInstanceOf[AttrPair]).map(_.asInstanceOf[AttrPair])
    val newDirectives = newMods.filter(_.isInstanceOf[Directive]).map(_.asInstanceOf[Directive])

    var i = 0

    // TODO kopirat OLD vrijednosti RxAttrValue u NEW ???

    // handle attributes
    locally {
      oldAttrPairs.foreach { ap =>
        ScalatagsAddons.applyAttrAndProp(existingElement, ap.a.name, None)
        existingElement.removeAttribute(ap.a.name)
      }

      existingElement.removeAttribute("class")

      newAttrPairs.foreach { ap =>
        ap.applyTo(existingElement)
        ScalatagsAddons.applyAttrAndProp(existingElement, ap.a.name, ap.v)
      }
    }

    // handle directives
    locally {
      newDirectives.foreach(_.applyTo(existingElement))
    }

    // handle children
    locally {
      //println("newChildrenFrags", newChildrenFrags, oldChildrenFrags)
      i = 0
      while (i < newChildrenFrags.length || i < oldChildrenFrags.length) {
        //println("Handling child ", i)
        update(
          existingElement,
          oldChildrenFrags.lift(i),
          newChildrenFrags.lift(i)
        )
        //println("DONE handling child ", i)
        i += 1
      }
    }

    //println("DONE handleHtmlTag", parent, oldNodeIdx, newTag, oldTag)
  }

  private def didChange(newFrag: Frag, oldFrag: Frag): (Boolean, String) =
    (newFrag, oldFrag) match {
      case (nf: HtmlTag, of: HtmlTag) =>
        (nf.tag != of.tag) -> ""
      case (nf: StringFrag, of: StringFrag) =>
        (nf.v != of.v) -> "StringFrag"
      case _ => true -> ""
    }

  private def isEmptyStringFrag(frag: Frag): Boolean =
    frag match {
      case sf: StringFrag => sf.v.trim.isEmpty
      case _              => false
    }

}
