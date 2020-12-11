package ba.sake.rxtags.todo

import java.util.UUID
import org.scalajs.dom
import ba.sake.rxtags._

class TodoService {

  val todos$ = initTodos()
  val filter$ = Var(TodoFilter.All)
  val toggleAll$ = Var(false)
  val editId$ = Var(Option.empty[UUID])

  todos$.attachAndFire { todos =>
    if (todos.length == 1) // synchronize with last element..
      toggleAll$.set(todos.head.completed)
    else if (todos.length > 1)
      toggleAll$.set(todos.forall(_.completed))
  }

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
    toggleAll$.set(s => !s)
    val ts = toggleAll$.now
    todos$.set { todos =>
      todos.map(_.copy(completed = ts))
    }
  }

  private def initTodos() = {
    import upickle.default._

    val TodosKey = "todos-RxTags"
    val savedTodosJson = dom.window.localStorage.getItem(TodosKey)
    val savedTodos =
      if (savedTodosJson == null) List.empty
      else read[List[Todo]](savedTodosJson)

    val initTodos$ = Var(savedTodos)
    initTodos$.attach { newValue =>
      dom.window.localStorage.setItem(TodosKey, write(newValue))
    }
    initTodos$
  }
}
