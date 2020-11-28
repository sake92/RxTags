package templates

import ba.sake.hepek.theme.bootstrap3.HepekBootstrap3BlogPage
import ba.sake.hepek.theme.bootstrap3.TocSettings
import ba.sake.hepek.theme.bootstrap3.TocType
import ba.sake.hepek.prismjs.{PrismDependencies, PrismSettings, Themes}
import ba.sake.hepek.Resources._
import utils.Imports.Bundle._

trait RxTagsStaticPage extends StaticPage with PrismDependencies {

  override def staticSiteSettings =
    super.staticSiteSettings
      .withIndexPage(site.Index)
      .withMainPages(site.Index, site.About)

  override def siteSettings =
    super.siteSettings.withName("RxTags")

  override def navbar = Some(Navbar)

  override def prismSettings = super.prismSettings
    .withTheme(Themes.Coy)
    .withLanguages("core", "clike", "scala", "java", "markup")

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

  override def tocSettings = Some(TocSettings(tocType = TocType.Scrollspy(offset = 45)))

  override def categoryPosts = {
    import site._
    List(Index, State, View)
  }
}
