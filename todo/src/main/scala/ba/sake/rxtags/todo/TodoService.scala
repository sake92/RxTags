package ba.sake.rxtags.todo

import java.util.UUID
import org.scalajs.dom
import ba.sake.rxtags._

class TodoService {

  val toggleAllState$ = Var(false)

  val todos$ : Var[List[Todo]] = initTodos()

  todos$.attachAndFire { todos =>
    if (todos.length == 1) // synchronize with last element..
      toggleAllState$.set(todos.head.completed)
    else if (todos.length > 1)
      toggleAllState$.set(todos.forall(_.completed))
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
    toggleAllState$.set(s => !s)
    val ts = toggleAllState$.now
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
      else read[List[Todo]](savedTodosJson).map(_.copy(editing = false))

    val initTodos$ = Var(savedTodos)
    initTodos$.attach { newValue =>
      dom.window.localStorage.setItem(TodosKey, write(newValue))
    }
    initTodos$
  }
}
