package ba.sake.rescala.todo

import ba.sake.scalajs_router.Router
import org.scalajs.dom

object Main {

  def main(args: Array[String]): Unit = {

    val root = dom.document.getElementById("main")
    val todoService = new TodoService
    val rootComponent = new MainComponent(todoService)
    root.appendChild(rootComponent.render)

    /*
    Router().withBaseUrl("/ReScala-todomvc").withListener {
      case "/active"    => MainComponent.todoFilter.set(TodoFilter.Active)
      case "/completed" => MainComponent.todoFilter.set(TodoFilter.Completed)
      case _            => MainComponent.todoFilter.set(TodoFilter.All)
    }.init()*/
  }
}
