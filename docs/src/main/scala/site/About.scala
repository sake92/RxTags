package site

import scalatags.Text.all._
import utils.Imports.Bundle._, Classes._

object About extends templates.RxTagsStaticPage {

  override def pageSettings = super.pageSettings.withTitle("About")

  override def pageContent =
    Panel.panel(
      Panel.Companion.Type.Info,
      s"""
        This is a custom page, using a different template.
        
        (almost impossible to make a mistake! :D)
      """.md,
      header = Some("About me")
    )

}
