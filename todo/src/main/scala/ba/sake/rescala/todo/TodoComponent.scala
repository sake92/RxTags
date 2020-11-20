package ba.sake.rescala.todo

import ba.sake.rxtags._
import org.scalajs.dom.ext.KeyValue
import org.scalajs.dom.raw.KeyboardEvent
import scalatags.JsDom.all._

case class TodoComponent(
    todoService: TodoService,
    todo: Todo
) {

  println(todo)

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
  toggleCompletedChannel.on { todo$.transform(_.toggled) }

  private val editInput = input(
    onblur := { () =>
      stopEditingChannel.set(())
    },
    onkeyup := { (e: KeyboardEvent) =>
      if (e.key == KeyValue.Enter) stopEditingChannel.set(())
    },
    value := todo$.map(_.name),
    cls := "edit"
  ).render

  def render: Frag = {
    val todoName$ = Val { span(todo$.get.name) }.asFrag
    val isChecked$ = Val { Option.when(todo$.get.completed)("checked") } // must use Option here !!!
    val completedCls$ = Val { if (todo$.get.completed) "completed" else "" }
    val editingCls$ = Val { if (isEdit$.get) "editing" else "" }

    println(todo$.get, isChecked$)

    li(cls := editingCls$, cls := completedCls$)(
      div(cls := "view")(
        input(
          onchange := { () =>
            toggleCompletedChannel.set(())
          },
          checked := isChecked$,
          cls := "toggle",
          tpe := "checkbox"
        ),
        label(ondblclick := { () =>
          startEditingChannel.set(())
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
    /*
    val newValue = editInput.value.trim
    if (newValue.nonEmpty) {
      todo$.transform(_.copy(name = newValue))
    }*/
  }
}
