package ba.sake.rescala.todo

import ba.sake.rxtags._
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.ext.KeyValue
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._

// <input>s are decoupled from its callbacks via Events
class MainComponent(todoService: TodoService) {
  private val todos$ = todoService.todos$

  val todoFilter$ : Var[TodoFilter] = Var(TodoFilter.All)
  private val todosFiltered$ = todoService.todos$.map {
    todos => todos.filter(todoFilter$.get.isValid)
  }

  private val addTodoEvent = Channel[KeyboardEvent]
  addTodoEvent.attach(addTodo)

  private val addInput = input(
    onkeyup := { (e: KeyboardEvent) =>
      if (e.key == KeyValue.Enter) addTodoEvent.set(e)
    },
    cls := "new-todo",
    placeholder := "What needs to be done?",
    autofocus
  ).render

  private val mainDisplay = todos$.map(todos => if (todos.isEmpty) "none" else "block")

  private val countFrag = todos$.map { todos =>
    val count = todos.count(!_.completed)
    val itemsLabel = if (count == 1) "item" else "items"
    div(strong(count), s" $itemsLabel left")
  }.asFrag

  def render =
    div(
      tag("section")(cls := "todoapp")(
        header(cls := "header")(
          h1("todos"),
          addInput
        ),
        tag("section")(cls := "main", css("display") := mainDisplay)(
          input(
            onclick := { () =>
              todoService.toggleAll()
            },
            id := "toggle-all",
            cls := "toggle-all",
            tpe := "checkbox"
          ),
          label(`for` := "toggle-all", "Mark all as complete"),
          ul(cls := "todo-list")(
            todosFiltered$.map { tf =>
              println("rennnnnnnnn " + tf)
              div(
                tf.map { t =>
                  TodoComponent(todoService, t).render
                }
              )
            }.asFrag
          )
        ),
        footer(cls := "footer", css("display") := mainDisplay)(
          span(cls := "todo-count")(countFrag),
          ul(cls := "filters")(
            li(a(data.navigate := "/", "All", cls := maybeSelected(TodoFilter.All))),
            li(a(data.navigate := "/active", "Active", cls := maybeSelected(TodoFilter.Active))),
            li(a(data.navigate := "/completed", "Completed", cls := maybeSelected(TodoFilter.Completed)))
          ),
          button(
            onclick := { () =>
              todoService.removeCompleted()
            },
            cls := "clear-completed",
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
    val newTodoName = e.target.asInstanceOf[Input].value.trim
    if (newTodoName.nonEmpty) {
      val newTodo = Todo(newTodoName)
      todoService.add(newTodo)
      addInput.value = ""
    }
  }

  private def maybeSelected(filter: TodoFilter) =
    todoFilter$.map { tf =>
      Option.when(tf == filter)("selected")
    }
}
