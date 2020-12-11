package ba.sake.rxtags.todo

trait TodoFilter {
  def isValid(todo: Todo): Boolean
}

object TodoFilter {
  val All: TodoFilter = _ => true
  val Completed: TodoFilter = todo => todo.completed
  val Active: TodoFilter = todo => !todo.completed
}
