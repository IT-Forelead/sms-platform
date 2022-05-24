import Dependencies._

lazy val projectSettings = Seq(
  version      := "1.0",
  scalaVersion := "2.13.8",
  organization := "IT-Forelead"
)

lazy val root = (project in file("."))
  .aggregate(server, tests)

lazy val server = (project in file("modules/server"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(projectSettings: _*)
  .settings(
    name              := "sms-platform",
    scalafmtOnCompile := true,
    libraryDependencies ++= coreLibraries,
    scalacOptions ++= CompilerOptions.cOptions,
    Test / compile / coverageEnabled    := true,
    Compile / compile / coverageEnabled := false
  )
  .settings(
    Docker / packageName := "sms-platform",
    dockerBaseImage      := "openjdk:11-jre-slim-buster",
    dockerUpdateLatest   := true
  )

lazy val tests = project
  .in(file("modules/tests"))
  .configs(IntegrationTest)
  .settings(projectSettings: _*)
  .settings(
    name := "sms-platform-test-suite",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    Defaults.itSettings,
    scalacOptions ++= CompilerOptions.cOptions,
    libraryDependencies ++= testLibraries,
    scalacOptions ++= CompilerOptions.cOptions
  )
  .dependsOn(server)

val runTests  = inputKey[Unit]("Runs tests")
val runServer = inputKey[Unit]("Runs server")

runServer := {
  (server / Compile / run).evaluated
}

runTests := {
  (tests / Test / test).value
}

Global / onLoad := (Global / onLoad).value.andThen(state => "project server" :: state)
