package ba.sake.rxtags.todo

import org.scalajs.dom
import org.scalajs.dom.ext.KeyValue
import scalatags.JsDom.all._
import ba.sake.rxtags._

case class TodoComponent(
    todoService: TodoService,
    todo: Todo
) {

  def render: Frag = {
    val isChecked = Option.when(todo.completed)("checked")
    val completedCls = Option.when(todo.completed)("completed")
    val editingCls = Option.when(todo.editing)("editing")
    val doFocus = Option.when(todo.editing)(todo.name.length -> todo.name.length)

    li(cls := editingCls, cls := completedCls)(
      div(cls := "view")(
        input(
          onchange := { (e: dom.Event) => toggle(e) },
          checked := isChecked,
          cls := "toggle",
          tpe := "checkbox"
        ),
        label(
          ondblclick := { () => startEditing() },
          span(todo.name)
        ),
        button(
          onclick := { () => todoService.remove(todo.id) },
          cls := "destroy"
        )
      ),
      input(
        doFocus.map(focus),
        onblur := { (e: dom.Event) => stopEditing(e, true) },
        onkeyup := { (e: dom.KeyboardEvent) =>
          if (e.key == KeyValue.Enter) stopEditing(e, true)
          else if (e.key == KeyValue.Escape) stopEditing(e, false)
        },
        value := todo.name,
        cls := "edit"
      )
    )
  }

  private def toggle(e: dom.Event): Unit = {
    val completed = e.target.asInstanceOf[dom.html.Input].checked
    val updatedTodo = todo.copy(completed = completed)
    todoService.update(updatedTodo)
    println("TOGLEDDDDD", updatedTodo)
  }

  private def startEditing(): Unit = {
    val updatedTodo = todo.startedEditing
    todoService.update(updatedTodo)
  }

  private def stopEditing(e: dom.Event, doUpdate: Boolean): Unit = {
    val editInput = e.target.asInstanceOf[dom.html.Input]
    if (doUpdate) {
      val newValue = editInput.value.trim
      editInput.value = newValue
      if (newValue.isEmpty) todoService.remove(todo.id)
      else {
        val updatedTodo = todo.finishedEditing.copy(name = newValue)
        todoService.update(updatedTodo)
      }
    } else { // when ESC is pressed, return to previous value
      editInput.value = todo.name
      editInput.blur()
    }
  }
}
