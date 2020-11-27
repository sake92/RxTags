package ba.sake.rxtags.example

import ba.sake.rxtags._
import org.scalajs.dom
import scalatags.JsDom.all._

// RxTags variable list
object Ex6 extends Example {

  case class CartItem(count: Int, name: String)

  val cartItems$ = Var(
    List(
      CartItem(10, "eggs"),
      CartItem(1, "milk"),
      CartItem(3, "bananas"),
      CartItem(2, "icecreams")
    )
  )

  def content = cartItems$.map { items =>
    ul(
      items.map { item =>
        li(
          b(item.count),
          s" ${item.name} ",
          button(onclick := delete(item))("Delete")
        )
      }
    )
  }.asFrag

  def delete(item: CartItem): (dom.Event => Unit) =
    e => {
      cartItems$.set { currentItems =>
        currentItems.filterNot(_ == item)
      }
    }
}
