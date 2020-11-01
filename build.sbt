inThisBuild(
  List(
    organization := "ba.sake",
    homepage := Some(url("https://github.com/sake92/RxTags")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(
      ScmInfo(url("https://github.com/sake92/RxTags"), "scm:git:git@github.com:sake92/RxTags.git")
    ),
    developers := List(
      Developer("sake92", "Sakib Hadžiavdić", "sakib@sake.ba", url("https://sake.ba")      )
    ),
    scalaVersion := "2.13.3",
    scalafmtOnCompile := true
  )
)

val core = (project in file("core"))
  .settings(
    name := "rxtags",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.8.6",
      "com.outr"    %%% "reactify"  % "4.0.2"
    )
  )
  .enablePlugins(ScalaJSPlugin)
