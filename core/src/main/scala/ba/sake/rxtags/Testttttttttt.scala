package ba.sake.rxtags.aaaaaaaaa

object Testttttttttt extends App {

  import ba.sake.rxtags._
  val aaaaa = Var("abc")
  aaaaa.transform(_ => "def")

  println(aaaaa.get)

}
