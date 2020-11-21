package ba.sake.rxtags.todo

import java.util.UUID
import org.scalajs.dom
import ba.sake.rxtags._

class TodoService {
  private val TodosKey = "TODOS"

  private val toggleAllState$ = Var(false)

  val todos$ : Var[List[Todo]] = initTodos()

  def add(todo: Todo): Unit = todos$.set(it => it.appended(todo))

  def update(updated: Todo): Unit = {
    todos$.set { it =>
      it.map(t => if (t.id == updated.id) updated else t)
    }
  }

  def remove(id: UUID): Unit = todos$.set(it => it.filterNot(_.id == id))

  def removeCompleted(): Unit = todos$.set(it => it.filterNot(_.completed))

  def toggleAll(): Unit = {
    toggleAllState$.set(s => !s)
    todos$.set {
      it => it.map(_.copy(completed = toggleAllState$.now))
    }
  }

  private def initTodos() = {
    import upickle.default._
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
