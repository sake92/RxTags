package ba.sake.rxtags

trait Reactive[+T] {

  def attach(f: T => Unit): Unit

  def on(f: => Unit): Unit = attach(_ => f)
}

trait Stateful[+T] extends Reactive[T] {

  def now: T

  def attachAndFire(f: T => Unit): Unit

  def map[R](f: T => R): Stateful[R]
}

// Val
object Val {
  def apply[T](initValue: => T): Val[T] = new Val(initValue)
}

final class Val[T] private (initValue: => T) extends Stateful[T] {

  private val rx = reactify.Val[T](initValue)

  override def now: T = rx.get

  override def attach(f: T => Unit): Unit = rx.attach(f)

  override def attachAndFire(f: T => Unit): Unit = rx.attachAndFire(f)

  override def map[R](f: T => R): Val[R] = Val(f(now))
}

// Var
object Var {
  def apply[T](initValue: => T): Var[T] = new Var(initValue)
}

final class Var[T] private (initValue: => T) extends Stateful[T] {

  private val rx = reactify.Var[T](initValue)

  override def now: T = rx.get

  override def attach(f: T => Unit): Unit = rx.attach(f)

  override def attachAndFire(f: T => Unit): Unit = rx.attachAndFire(f)

  override def map[R](f: T => R): Var[R] = Var(f(now))

  def set(f: => T): Unit = rx.set(f)

  def set(f: T => T): Unit = rx.set(f(now))
}

// Channel
object Channel {
  def apply[T]: Channel[T] = new Channel()
}

final class Channel[T] private () extends Reactive[T] {

  private val rx = reactify.Channel[T]

  override def attach(f: T => Unit): Unit = rx.attach(f)

  def fire(f: => T): Unit = rx.set(f)
}
