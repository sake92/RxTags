package ba.sake

package object rxtags extends RxFrags with RxAttrValues with RxStyleValues with ScalatagsAddons with Directives {

  implicit class StatefulSeqOps[T](rx: Stateful[Seq[T]]) {
    def map2[R](f: T => R): Stateful[Seq[R]] = rx.map(cc => cc.map(f))
  }

}
