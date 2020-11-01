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
      maybeNewNode: Option[Frag] = None,
      maybeOldNode: Option[Frag] = None,
      oldNodeIdx: Int = 0
  ): Unit =
    (maybeNewNode, maybeOldNode) match {
      case (Some(newNode), None) => parent.appendChild(
          createElement(newNode)
        )
      case (None, _) => parent.removeChild(
          parent.childNodes(oldNodeIdx)
        )
      case (Some(newNode), Some(oldNode)) =>
        if (didChange(newNode, oldNode)) {
          parent.replaceChild(
            createElement(newNode),
            parent.childNodes(oldNodeIdx)
          )
        } else { // if not changed, check children also
          // TODO handle SeqFrag
          if (!newNode.isInstanceOf[HtmlTag]) {
            return
          }

          val newNodeChildren = newNode.asInstanceOf[HtmlTag].modifiers.flatten
          val oldNodeChildren = oldNode.asInstanceOf[HtmlTag].modifiers.flatten
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
    }

  def didChange(newNode: Frag, oldNode: Frag): Boolean =
    if (newNode.getClass != oldNode.getClass) true
    else newNode match {
      case _: StringFrag => true
      case _: RawFrag    => true
      case _: SeqFrag[_] => false
      case _ =>
        val node1 = newNode.asInstanceOf[HtmlTag]
        val node2 = oldNode.asInstanceOf[HtmlTag]
        node1.tag != node2.tag
    }
}
