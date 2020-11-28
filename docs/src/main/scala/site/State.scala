package site

import scalatags.Text.all._
import utils.Imports._, Bundle._

object State extends templates.RxTagsBlogPage {

  override def pageSettings = super.pageSettings.withTitle("State")

  override def blogSettings = super.blogSettings.withSections(rxVarsSection, rxValsSection)

  def rxVarsSection = Section(
    "Rx Vars",
    """
      Every frontend app needs to maintain some state.  
      State usually has an initial value, and it can **change over time**.  
      
      In RxTags we use **reactive variables**: `Var[T]`.  
      `Var[T]` has 2 important methods:
      - `now`, returns the current value of variable
      - `set`, sets the new value of variable
      
      For example, we could have a state variable that holds a name that user types.  
      We can get its current value, and set it later:
      ```scala
      val name$ = Var("Tim")
      println(name$.now) // "Tim"
      
      name$.set("Jane")
      println(name$.now) // "Jane"
      ```
      
      ---
      When we need a *new variable based on existing one*, we can use `map`:
      ```scala
      val nameUpper$ = name$.map(n => n.toUpperCase)
      ```
      The `nameUpper$` variable now holds a value which is equal to uppercased `name$` value.  
      This gets very useful when we need to display a `Var` in HTML.  
      We will see that in the next section.
      
      ---
      When we need to listen for a variable change, we use the `attach` method:
      ```scala
      name$.attach { n =>
        println(s"Name is now: $n")
      }
      ```
    """.md
  )

  def rxValsSection = Section(
    "Rx Vals",
    """
      We also have Rx `Val`s.  
      These are reactive values that **cannot be set**.   
      They are just a composition of other `Var`s/`Val`s.
      
      Example:
      ```scala
      val nameUpper$ = Val {
        val current = name$.now
        if (current.isEmpty) "<EMPTY>"
        else current.toUpperCase
      }
      ```
      Here we see again the `nameUpper$` variable.  
      Now it has a bit of logic in there.  
      But we could achieve the same result with `map`...
      
      The difference it makes is when you have **multiple dependencies**:
      
      ```scala
      val name$ = Var("Tim")
      val age$ = Var(29)
    
      val nameAndAge$ = Val {
        s"${name$.now} is ${age$.now} years old."
      }
      ```
    """.md
  )
}
