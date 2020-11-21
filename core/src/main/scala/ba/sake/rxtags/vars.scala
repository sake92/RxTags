package ba.sake.rxtags

trait Reactive[T] {

  def attach(f: T => Unit): Unit

  def on(f: => Unit): Unit = attach(_ => f)
}

trait Stateful[T] {

  def now: T

  def attachAndFire(f: T => Unit): Unit
}

// Val
object Val {
  def apply[T](f: => T): Val[T] = new Val(f)
}

class Val[T](f: => T) extends Reactive[T] with Stateful[T] {

  private val rx = reactify.Val[T](f)

  override def now: T = rx.get

  override def attach(f: T => Unit): Unit = rx.attach(f)

  override def attachAndFire(f: T => Unit): Unit = rx.attachAndFire(f)

  def map[R](f: T => R): Val[R] = Val(f(now))
}

// Var
object Var {
  def apply[T](f: => T): Var[T] = new Var(f)
}

class Var[T](f: => T) extends Reactive[T] with Stateful[T] {

  private val rx = reactify.Var[T](f)

  override def now: T = rx.get

  override def attach(f: T => Unit): Unit = rx.attach(f)

  override def attachAndFire(f: T => Unit): Unit = rx.attachAndFire(f)

  def set(f: => T): Unit = rx.set(f)

  def set(f: T => T): Unit = rx.set(f(rx.get))

  def map[R](f: T => R): Var[R] = Var(f(rx.get))
}

// Var
object Channel {
  def apply[T]: Channel[T] = new Channel
}

class Channel[T] extends Reactive[T] {

  private val rx = reactify.Channel[T]

  def fire(f: => T): Unit = rx.set(f)

  def attach(f: T => Unit): Unit = rx.attach(f)
}
