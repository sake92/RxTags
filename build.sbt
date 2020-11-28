inThisBuild(
  List(
    organization := "ba.sake",
    homepage := Some(url("https://github.com/sake92/RxTags")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(
      ScmInfo(url("https://github.com/sake92/RxTags"), "scm:git:git@github.com:sake92/RxTags.git")
    ),
    developers := List(
      Developer("sake92", "Sakib Hadžiavdić", "sakib@sake.ba", url("https://sake.ba"))
    ),
    scalaVersion := "2.13.3",
    skip in publish := true,
    scalafmtOnCompile := true
  )
)

lazy val core = (project in file("core"))
  .settings(
    name := "rxtags",
    skip in publish := false,
    libraryDependencies ++= Seq(
      "ba.sake" %%% "scalatags" % "0.9.2-rx2",
      "com.outr" %%% "reactify" % "4.0.2"
    )
  )
  .enablePlugins(ScalaJSPlugin)

lazy val docs = (project in file("docs"))
  .settings(
    libraryDependencies ++= Seq(
      "ba.sake" %% "hepek" % "0.8.5+2-5ea855d0-SNAPSHOT"
    ),
    (Compile / hepek) := {
      // (examples / Compile / fastOptJS).value // generate examples JS
      (Compile / fastOptJS).value // generate docs JS
      WebKeys.assets.value // run assets
      (Compile / hepek).value
    },
    resolvers += Resolver.sonatypeRepo("snapshots"),
    openIndexPage := openIndexPageTask.value,
    scalaJSUseMainModuleInitializer := true,
    // sbt-web
    // https://stackoverflow.com/a/29375359/4496364
    (Compile / fastOptJS / artifactPath) :=
      (Assets / WebKeys.public).value / "site" / "scripts" / ((moduleName in fastOptJS).value + "-fastopt.js"),
    WebKeys.webModulesLib := "site/lib"
  )
  .dependsOn(examples)
  .enablePlugins(HepekPlugin, SbtWeb, ScalaJSPlugin)

lazy val examples = (project in file("examples"))
  .settings(
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(core)
  .enablePlugins(ScalaJSPlugin)

lazy val todo = (project in file("todo"))
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "1.2.0",
      "ba.sake" %%% "scalajs-router" % "0.0.5"
    )
  )
  .dependsOn(core)
  .enablePlugins(ScalaJSPlugin)

val openIndexPage = taskKey[Unit]("Opens index.html")

val openIndexPageTask = Def.taskDyn {
  Def.task {
    java.awt.Desktop.getDesktop
      .browse(new File(hepekTarget.value + "/site/index.html").toURI)
  }
}
