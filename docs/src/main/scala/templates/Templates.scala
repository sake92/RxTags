package templates

import scalatags.Text.all._
import ba.sake.hepek.fontawesome5.FADependencies
import ba.sake.hepek.fontawesome5.FA
import ba.sake.hepek.theme.bootstrap3.HepekBootstrap3BlogPage
import ba.sake.hepek.theme.bootstrap3.TocSettings
import ba.sake.hepek.theme.bootstrap3.TocType
import ba.sake.hepek.prismjs.{PrismConsts, PrismDependencies, PrismSettings, Themes}
import ba.sake.hepek.Resources._
import utils.Imports.Bundle._

trait RxTagsStaticPage extends StaticPage with PrismDependencies with FADependencies {
  // dont have to remember ordering of these.. filter below!
  private val hlLangs = Set("core", "clike", "scala", "java", "markup")

  override def staticSiteSettings =
    super.staticSiteSettings
      .withIndexPage(site.Index)

  override def siteSettings =
    super.siteSettings.withName("RxTags")

  override def navbar = Some(Navbar)

  override def prismSettings = super.prismSettings
    .withTheme(Themes.Coy)
    .withLanguages(PrismConsts.languages.filter(hlLangs))

  override def bootstrapDependencies =
    super.bootstrapDependencies.withCssDependencies(
      Dependencies()
        .withDeps(Dependency("readable/bootstrap.min.css", bootstrapSettings.version, "bootswatch"))
    )

  override def styleURLs = super.styleURLs.appended(styles.css("main").ref)

  override def scriptURLs =
    super.scriptURLs
      .appended(scripts.js("docs-fastopt").ref)
}

trait RxTagsBlogPage extends RxTagsStaticPage with HepekBootstrap3BlogPage {

  override def pageHeader = None

  override def tocSettings = Some(TocSettings(tocType = TocType.Scrollspy(offset = 71)))

  override def categoryPosts = {
    import site._
    List(Index, State, View)
  }

  override def pageContent = {
    import Classes._
    frag(
      super.pageContent,
      footer(txtAlignCenter, bgInfo, cls := "navbar-fixed-bottom")(
        hyperlink("https://github.com/sake92/RxTags", btnClass)(FA.github()),
        hyperlink("https://gitter.im/sake92/RxTags", btnClass)(FA.gitter())
      )
    )
  }
}
