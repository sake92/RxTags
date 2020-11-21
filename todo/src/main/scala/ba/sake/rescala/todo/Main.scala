package ba.sake.rescala.todo

import ba.sake.scalajs_router.Router
import org.scalajs.dom

object Main {

  def main(args: Array[String]): Unit = {

    val root = dom.document.getElementById("main")
    val todoService = new TodoService
    val rootComponent = new MainComponent(todoService)
    root.appendChild(rootComponent.render)

    Router().withBaseUrl("/RxTags/todo").withListener {
      case "/active"    => rootComponent.todoFilter$.set(TodoFilter.Active)
      case "/completed" => rootComponent.todoFilter$.set(TodoFilter.Completed)
      case _            => rootComponent.todoFilter$.set(TodoFilter.All)
    }.init()
  }
}
