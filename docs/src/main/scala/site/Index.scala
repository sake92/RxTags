package site

import scalatags.Text.all._
import utils.Imports._, Bundle._

object Index extends templates.RxTagsBlogPage {

  override def pageSettings = super.pageSettings.withTitle("Basics")

  override def blogSettings = super.blogSettings.withSections(basicsSection)

  val basicsSection = Section(
    "Basics",
    div(
      s"""
      RxTags uses [ScalaTags](https://www.lihaoyi.com/scalatags) library behind the scenes,
      so it is very useful to be familiar with it.  
      It is pretty simple library for building HTML, similar to JSX.  
      A big difference is that it is just Scala, no special preprocessor needed.
      
      Let's see an example:
      """.md,
      chl.scala("""
        import scalatags.JsDom.all._
        
        val number = 123

        def content = div(
          h4("ScalaTags example"),
          footer(cls := "abc")(
            s"Hello, visitor number: $number"
          )
        )
      """),
      """
      We can see that `div` is just a simple function call.  
      It gets children HTML tags passed as parameters.  
      ScalaTags calls these visible parts `Frag`s, fragments.
      
      Attributes are assigned with the `:=` operator.
      
      We can use arbitrary Scala code, like string interpolation in our example.  
      Feel free to use lists and map them to HTML, use helper functions, classes etc.
      
      Then we will use ScalaJS to attach that ScalaTags snippet as a real DOM node:
      """.md,
      chl.scala("""
        val root = dom.document.getElementById("root")
        root.appendChild(content.render)
      """),
      b("Result:"),
      Panel.panel(Panel.Companion.Type.Default, div(id := "ex1"))
    )
  )

}
