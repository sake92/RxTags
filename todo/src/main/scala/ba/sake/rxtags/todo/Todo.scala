package ba.sake.rxtags.todo

import java.util.UUID

case class Todo(name: String, completed: Boolean = false, editing: Boolean = false, id: UUID = UUID.randomUUID()) {
  def toggled: Todo = copy(completed = !completed)

  def startedEditing: Todo = copy(editing = true)
  def finishedEditing: Todo = copy(editing = false)
}

object Todo {
  import upickle.default.{ReadWriter => RW, macroRW}
  implicit val rw: RW[Todo] = macroRW[Todo]
}
