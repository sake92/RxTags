package ba.sake.rxtags.todo

import java.util.UUID

case class Todo(name: String, completed: Boolean = false, id: UUID = UUID.randomUUID())

object Todo {
  import upickle.default.{ReadWriter => RW, macroRW}
  implicit val rw: RW[Todo] = macroRW[Todo]
}
