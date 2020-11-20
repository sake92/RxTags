package ba.sake.rxtags.example

import ba.sake.rxtags._
import org.scalajs.dom
import scalatags.JsDom.all._

object ExAll {

  // probat sve moguće rx-ove

  val className$ = Var(Option.empty[String])

  def content(): Frag =
    div(
      button(onclick := toggleClass())("Toggle class"),
      div(
        div(cls := className$)("aaaaaaa"),
        hr,
        className$.map(cn => cn.getOrElse("???")).asFrag,
        hr,
        className$.map { cn =>
          val bla = cn.getOrElse("???")
          div(bla)
        }.asFrag,
        hr,
        className$.map { cn =>
          val bla = cn.map(v => s"<span>$v</span>").getOrElse("<p>???</p>")
          raw(bla)
        }.asFrag,
        hr,
        className$.map { cn =>
          val res = cn.map(v => div(v)) ++ Seq(div("bbbbbbbb")) ++ cn.map(v => div(v))
          res.toSeq
        }.asFrag,
        hr,
        className$.map { cn =>
          val res = cn.map(v => div(v)) ++ Seq(div("ccccc")) ++ cn.map(v => div(v))
          res.toSeq
        }.asFrag
      )
    )

  def toggleClass(): (dom.MouseEvent => Unit) =
    e => {
      className$.transform { cn =>
        if (cn.isDefined) None else Some("active")
      }
    }
}
