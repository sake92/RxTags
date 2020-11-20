package ba.sake

package object rxtags
    extends RxFrags
    with RxAttrValues
    with RxStyleValues
    with RxAddons
    with ScalatagsAddons {

  // TODO make RxTags Var-s,
  // just delegate to reactify! :) no implicits bullshit

  // Val (set, now), Var, Channel (fire)
  // Var.map vraća Val !!
  // Var.set i set(T => T)

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
  import org.scalajs.dom
  import scalatags.JsDom.all.{Attr, AttrValue}
  import scalatags.generic

  // 1. it's not enough to add/remove "checked", we need to remove the propery also..
  // 2. If it's a reactive class, we just delegate to its handler,
  // in order to not break RxValue classes...
  implicit def optionAttrValue[T](implicit ev: AttrValue[T]): generic.AttrValue[dom.Element, Option[T]] =
    new AttrValue[Option[T]] {
      override def apply(t: dom.Element, a: Attr, v: Option[T]): Unit = {
        v match {
          case Some(value) =>
            ev.apply(t, a, value)
            if (a.name == "checked") {
              t.asInstanceOf[dom.raw.HTMLInputElement].checked = true
            }
          case None =>
            if (a.name == "class") {
              ev.apply(t, a, "".asInstanceOf[T])
            } else {
              t.removeAttribute(a.name)
              if (a.name == "checked") {
                t.asInstanceOf[dom.raw.HTMLInputElement].checked = false
              }
            }
        }
      }
    }
}
