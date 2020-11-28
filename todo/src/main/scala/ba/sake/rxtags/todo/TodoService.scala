package ba.sake.rxtags.todo

import java.util.UUID
import org.scalajs.dom
import ba.sake.rxtags._

class TodoService {

  private val toggleAllState$ = Var(false)

  val todos$ : Var[List[Todo]] = initTodos()

  def add(todo: Todo): Unit =
    todos$.set(todos => todos.appended(todo))

  def update(updated: Todo): Unit = todos$.set { todos =>
    todos.map(t => if (t.id == updated.id) updated else t)
  }

  def remove(id: UUID): Unit =
    todos$.set(it => it.filterNot(_.id == id))

  def removeCompleted(): Unit =
    todos$.set(it => it.filterNot(_.completed))

  def toggleAll(): Unit = {
    toggleAllState$.set(s => !s)
    todos$.set {
      todos => todos.map(_.copy(completed = toggleAllState$.now))
    }
  }

  private def initTodos() = {
    import upickle.default._

    val TodosKey = "TODOS"
    val savedTodosJson = dom.window.localStorage.getItem(TodosKey)
    val todos =
      if (savedTodosJson == null)
        List(Todo("Create a TodoMVC template", completed = true), Todo("Rule the web"))
      else read[List[Todo]](savedTodosJson)

    val initTodos$ = Var(todos)
    initTodos$.attach { newValue =>
      dom.window.localStorage.setItem(TodosKey, write(newValue))
    }
    initTodos$
  }
}
