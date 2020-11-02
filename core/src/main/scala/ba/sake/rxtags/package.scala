package ba.sake

package object rxtags
    extends RxFrags
    with RxAttrValues
    with RxStyleValues
    with RxAddons
    with ScalatagsAddons {

  type Var[T] = reactify.Var[T]
  type Val[T] = reactify.Val[T]
  type Channel[T] = reactify.Channel[T]

  val Var = reactify.Var
  val Val = reactify.Val
  val Channel = reactify.Channel

}

trait RxAddons {
  import reactify._

  implicit class VarOps[T](private val rx: Var[T]) {

    def transform(f: T => T): Unit = {
      val newValue = f(rx.get)
      rx.set(newValue)
    }
  }
}

trait ScalatagsAddons {
  import org.scalajs.dom.Element
  import scalatags.JsDom.all.{Attr, AttrValue}
  import scalatags.generic

  implicit def optionAttrValue[T](implicit ev: AttrValue[T]): generic.AttrValue[Element, Option[T]] =
    new AttrValue[Option[T]] {
      override def apply(t: Element, a: Attr, v: Option[T]): Unit = {
        v match {
          case Some(value) => ev.apply(t, a, value)
          case None => a.namespace match {
              case None     => t.removeAttribute(a.name)
              case Some(ns) => t.removeAttributeNS(ns.uri, a.name)
            }
        }
      }
    }
}
