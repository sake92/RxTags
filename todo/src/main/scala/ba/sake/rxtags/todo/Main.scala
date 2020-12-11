package ba.sake.rxtags.todo

import ba.sake.scalajs_router.Router

object Main {

  def main(args: Array[String]): Unit = {

    val router = Router()
    val todoService = new TodoService
    val mainComponent = new MainComponent(todoService, router)

    // always return MainComponent
    val routes: Router.Routes = _ => mainComponent

    val listener: Router.Listener = {
      case "/active"    => todoService.filter$.set(TodoFilter.Active)
      case "/completed" => todoService.filter$.set(TodoFilter.Completed)
      case _            => todoService.filter$.set(TodoFilter.All)
    }

    router.withRoutesData("main", routes).withListener(listener).init()
  }
}
