package ba.sake.rescala.todo

trait TodoFilter {
  def isValid(todo: Todo): Boolean
}

object TodoFilter {
  val All: TodoFilter = todo => true
  val Completed: TodoFilter = todo => todo.completed
  val Active: TodoFilter = todo => !todo.completed
}
