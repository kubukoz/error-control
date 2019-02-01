val Scala_211 = "2.11.12"
val Scala_212 = "2.12.8"

inThisBuild(
  List(
    scalaVersion := Scala_211,
    organization := "com.kubukoz",
    homepage := Some(url("https://github.com/kubukoz/error-control")),
    licenses := List("MIT" -> url("https://github.com/kubukoz/error-control/blob/master/LICENSE")),
    developers := List(
      Developer(
        "kubukoz",
        "Jakub Kozłowski",
        "kubukoz@gmail.com",
        url("https://kubukoz.com")
      )
    )
  ))

val compilerPlugins = List(
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full),
  compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")
)

val commonSettings = Seq(
  crossScalaVersions := List(Scala_211, Scala_212),
  scalacOptions ++= Options.all,
  fork in Test := true,
  name := "error-control",
  libraryDependencies ++= compilerPlugins
)

val core = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
              "org.typelevel" %% "cats-core" % "1.6.0"
            ),
            name += "-core")

val laws = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
      "org.typelevel" %% "cats-laws" % "1.6.0"
    ),
    name += "-laws"
  )
  .dependsOn(core)

val tests = project
  .settings(commonSettings)
  .settings(
    name += "-tests",
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-testkit" % "1.6.0" % Test
    )
  )
  .dependsOn(laws)

val errorControl =
  project
    .in(file("."))
    .settings(name := "error-control", publishArtifact := false)
    .aggregate(core, laws, tests)
    .dependsOn(core, laws, tests)
