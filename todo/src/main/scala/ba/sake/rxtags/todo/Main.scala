package ba.sake.rxtags.todo

import ba.sake.scalajs_router.Router

object Main {

  def main(args: Array[String]): Unit = {

    val router = Router()
    val todoService = new TodoService
    val mainComponent = new MainComponent(todoService, router)

    val routes: Router.Routes = {
      case "/active" =>
        mainComponent.todoFilter$.set(TodoFilter.Active)
        mainComponent
      case "/completed" =>
        mainComponent.todoFilter$.set(TodoFilter.Completed)
        mainComponent
      case _ =>
        mainComponent.todoFilter$.set(TodoFilter.All)
        mainComponent
    }

    router.withRoutesData("main", routes).init()
  }
}
