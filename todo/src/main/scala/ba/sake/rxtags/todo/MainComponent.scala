package ba.sake.rxtags.todo

import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.ext.KeyValue
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._
import ba.sake.rxtags._

class MainComponent(todoService: TodoService) {

  val todoFilter$ = Var(TodoFilter.All)

  private val todos$ = todoService.todos$

  private val todosFiltered$ = todos$.map {
    todos => todos.filter(todoFilter$.now.isValid)
  }

  private val mainDisplay$ = todos$.map(todos => if (todos.isEmpty) "none" else "block")
  private val clearCompletedDisplay$ = todos$.map(todos => if (todos.exists(_.completed)) "block" else "none")
  private val toggleAllChecked$ = Val { Option.when(todoService.toggleAllState$.now)("checked") }

  def render =
    div(
      tag("section")(cls := "todoapp")(
        header(cls := "header")(
          h1("todos"),
          input(
            onkeyup := { (e: KeyboardEvent) =>
              if (e.key == KeyValue.Enter) addTodo(e)
            },
            cls := "new-todo",
            placeholder := "What needs to be done?",
            autofocus
          )
        ),
        tag("section")(cls := "main", css("display") := mainDisplay$)(
          input(
            tpe := "checkbox",
            checked := toggleAllChecked$,
            onclick := { () =>
              todoService.toggleAll()
            },
            id := "toggle-all",
            cls := "toggle-all"
          ),
          label(`for` := "toggle-all", "Mark all as complete"),
          ul(cls := "todo-list")(
            todosFiltered$.map { tf =>
              tf.map { t =>
                TodoComponent(todoService, t).render
              }
            }.asFrag
          )
        ),
        footer(cls := "footer", css("display") := mainDisplay$)(
          span(cls := "todo-count")(
            todos$.map { todos =>
              val count = todos.count(!_.completed)
              val itemsLabel = if (count == 1) "item" else "items"
              frag(strong(count), s" $itemsLabel left")
            }.asFrag
          ),
          ul(cls := "filters")(
            li(a(data.navigate := "/", cls := selectedCls(TodoFilter.All))("All")),
            li(a(data.navigate := "/active", cls := selectedCls(TodoFilter.Active))("Active")),
            li(a(data.navigate := "/completed", cls := selectedCls(TodoFilter.Completed))("Completed"))
          ),
          button(
            onclick := { () =>
              todoService.removeCompleted()
            },
            cls := "clear-completed",
            css("display") := clearCompletedDisplay$,
            "Clear completed"
          )
        )
      ),
      footer(cls := "info")(
        p("Double-click to edit a todo"),
        p("Created by ", a(href := "https://sake.ba")("Sakib Hadžiavdić")),
        p("Part of ", a(href := "http://todomvc.com")("TodoMVC"))
      )
    ).render

  private def addTodo(e: KeyboardEvent): Unit = {
    val addInput = e.target.asInstanceOf[Input]
    val newTodoName = addInput.value.trim
    if (newTodoName.nonEmpty) {
      val newTodo = Todo(newTodoName)
      todoService.add(newTodo)
      addInput.value = ""
    }
  }

  private def selectedCls(filter: TodoFilter) =
    todoFilter$.map { tf =>
      Option.when(tf == filter)("selected")
    }
}
