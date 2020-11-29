package ba.sake.rxtags.todo

import ba.sake.rxtags._
import org.scalajs.dom.ext.KeyValue
import org.scalajs.dom.raw.KeyboardEvent
import scalatags.JsDom.all._

case class TodoComponent(
    todoService: TodoService,
    todo: Todo
) {

  private val todo$ = Var(todo)
  private val isEdit$ = Var(false)

  todo$.attach { updated =>
    todoService.update(updated)
  }

  private val editInput = input(
    onblur := { () => stopEditing(true) },
    onkeyup := { (e: KeyboardEvent) =>
      if (e.key == KeyValue.Enter) stopEditing(true)
      else if (e.key == KeyValue.Escape) stopEditing(false)
    },
    value := todo.name,
    cls := "edit"
  ).render

  def render: Frag = {
    val todoName$ = todo$.map(_.name).asFrag
    val isChecked$ = Val { Option.when(todo$.now.completed)("checked") } // must use Option here !!!
    val completedCls$ = Val { Option.when(todo$.now.completed)("completed") }
    val editingCls$ = Val { Option.when(isEdit$.now)("editing") }

    li(cls := editingCls$, cls := completedCls$)(
      div(cls := "view")(
        input(
          onchange := { () =>
            todo$.set(_.toggled)
          },
          checked := isChecked$,
          cls := "toggle",
          tpe := "checkbox"
        ),
        label(ondblclick := startEditing)(span(todoName$)),
        button(
          onclick := { () =>
            todoService.remove(todo.id)
          },
          cls := "destroy"
        )
      ),
      editInput
    )
  }

  private def startEditing = () => {
    isEdit$.set(true)
    editInput.focus()
    editInput.selectionStart = editInput.value.length
  }

  private def stopEditing(doUpdate: Boolean): Unit = {
    isEdit$.set(false)
    editInput.value = editInput.value.trim
    if (doUpdate) {
      val newValue = editInput.value
      if (newValue.isEmpty) todoService.remove(todo.id)
      else todo$.set(_.copy(name = newValue))
    } else { // when ESC is pressed, return to previous value
      editInput.value = todo$.now.name
    }
  }
}
