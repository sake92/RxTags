package ba.sake.rxtags.todo

import org.scalajs.dom
import ba.sake.scalajs_router.Router

object Main {

  def main(args: Array[String]): Unit = {

    val todoService = new TodoService
    val mainComponent = new MainComponent(todoService)

    val root = dom.document.getElementById("main")
    root.appendChild(mainComponent.render)

    Router().withBaseUrl("/RxTags/todo").withListener {
      case "/active"    => mainComponent.todoFilter$.set(TodoFilter.Active)
      case "/completed" => mainComponent.todoFilter$.set(TodoFilter.Completed)
      case _            => mainComponent.todoFilter$.set(TodoFilter.All)
    }.init()
  }
}
