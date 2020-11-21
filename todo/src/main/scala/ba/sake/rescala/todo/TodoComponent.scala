package ba.sake.rescala.todo

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

  private val startEditingChannel = Channel[Unit]
  startEditingChannel.on { startEditing() }

  private val stopEditingChannel = Channel[Unit]
  stopEditingChannel.on { stopEditing() }

  private val toggleCompletedChannel = Channel[Unit]
  toggleCompletedChannel.on { todo$.set(_.toggled) }

  private val editInput = input(
    onblur := { () =>
      stopEditingChannel.fire(())
    },
    onkeyup := { (e: KeyboardEvent) =>
      if (e.key == KeyValue.Enter) stopEditingChannel.fire(())
    },
    value := todo$.map(_.name),
    cls := "edit"
  ).render

  def render: Frag = {
    val todoName$ = Val { span(todo$.now.name) }.asFrag
    val isChecked$ = Val { Option.when(todo$.now.completed)("checked") } // must use Option here !!!
    val completedCls$ = Val { if (todo$.now.completed) "completed" else "" }
    val editingCls$ = Val { if (isEdit$.now) "editing" else "" }

    li(cls := editingCls$, cls := completedCls$)(
      div(cls := "view")(
        input(
          onchange := { () =>
            toggleCompletedChannel.fire(())
          },
          checked := isChecked$,
          cls := "toggle",
          tpe := "checkbox"
        ),
        label(ondblclick := { () =>
          startEditingChannel.fire(())
        })(todoName$),
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

  private def startEditing(): Unit = {
    isEdit$.set(true)
    editInput.focus()
    editInput.selectionStart = editInput.value.length
  }

  private def stopEditing(): Unit = {
    isEdit$.set(false)
    val newValue = editInput.value.trim
    if (newValue.nonEmpty) {
      todo$.set(_.copy(name = newValue))
    }
  }
}
