package ba.sake.rescala.todo

import java.util.UUID

case class Todo(name: String, completed: Boolean = false, id: UUID = UUID.randomUUID()) {
  def toggled: Todo = copy(completed = !completed)
}

object Todo {
  import upickle.default.{ReadWriter => RW, macroRW}
  implicit val rw: RW[Todo] = macroRW[Todo]
}
