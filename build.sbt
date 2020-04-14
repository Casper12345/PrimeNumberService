
lazy val commonSettings = (
  scalaVersion := "2.12.7"
)

lazy val finagleProject = (project in file("finagle_project"))
  .settings(
    name := "finagle_project",
    commonSettings,
    libraryDependencies ++= Seq(
      Dependencies.FinagleHttp,
      Dependencies.FinagleThrift,
      Dependencies.TwitterServer,
      Dependencies.SprayJson,
      Dependencies.TypeSafeConfig,
      Dependencies.ScalaTest
    ) ++ Dependencies.FinatraTestDependencies
  ).enablePlugins(ScroogeSBT)

