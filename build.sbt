import Dependencies._

/**
 * https://stackoverflow.com/questions/23188771/confused-how-to-setup-a-multi-project-sbt-project
 * http://xerial.org/blog/2014/03/26/buidling-multi-module-projects-in-sbt/
 */

lazy val appDbtile = (project in file("dbtile")).
  settings(Commons.settings: _*).
  settings(
    version := "1.0",
    name := "dbtile",
    libraryDependencies ++= dbtileDeps
  )

lazy val appWeb = (project in file("web")).
  settings(Commons.settings: _*).
  settings(
    version := "1.0",
    name := "dbtile-web",
    libraryDependencies ++= webDeps
  ).
  dependsOn(appDbtile)
