package site

import scalatags.Text.all._
import utils.Imports.Bundle._
import utils.Imports._

object View extends templates.RxTagsBlogPage {

  override def pageSettings = super.pageSettings.withTitle("View")

  override def blogSettings = super.blogSettings.withSections(rxDomSection)

  def rxDomSection = Section(
    "Rx DOM",
    div(
      s"""
      Now when we have some state, we need to show it to the user.  
      We need to add some additional imports:
      ```scala
      import org.scalajs.dom
      import ba.sake.rxtags._
      ```
      
      Here is an example of a ticker, it counts the number of seconds passed:
      """.md,
      chl.scala.withLineHighlight("1,4,10")(
        """
        val ticker$ = Var(0)

        dom.window.setInterval(
          () => { ticker$.set(t => t + 1) },
          1000
        )
      
        def content = div(
          "Ticker: ",
          ticker$
        )
      """
      ),
      b("Result:"),
      Panel.panel(Panel.Companion.Type.Default, div(id := "ex5")),
      """
      At line <mark>1</mark> we declare a reactive variable, `Var[Int]` with an initial value of `0`.  
      
      Next, at line <mark>4</mark> we increment it, every second.  
      
      Finally, at line <mark>10</mark> we render it to HTML.  
      Any `Var[Frag]` can be put just as ordinary ScalaTags! :)  
      There are also handy implicit conversions for `Var[String]`, `Var[Int]` (in example above) and similar.
      
      ---
      Here is another example, using an event handler:
      """.md,
      chl.scala.withLineHighlight("1,5,7,15")(
        """
        val name$ = Var("")

        def content = div(
          "Please enter your name: ",
          input(onkeyup := updateName),
          br,
          "Your name: ",
          name$
        )
      
        def updateName: (dom.KeyboardEvent => Unit) =
          e => {
            val inputField = e.target.asInstanceOf[Input]
            name$.set(inputField.value)
          }
      """
      ),
      b("Result:"),
      Panel.panel(Panel.Companion.Type.Default, div(id := "ex3"))
    )
  )

}
