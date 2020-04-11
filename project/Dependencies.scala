import sbt._

object Dependencies {
  val FinagleHttp = "com.twitter" %% "finagle-http" % "20.3.0"
  val FinagleThrift = "com.twitter" %% "finagle-thrift" % "20.3.0"
  val TwitterServer = "com.twitter" %% "twitter-server" % "20.3.0"
  val SprayJson = "io.spray" %% "spray-json" % "1.3.5"
  val ScalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % Test
  val FinatraTestDependencies = Seq(
    "com.twitter" %% "finatra-http" % "20.3.0" % "test" classifier "tests",
    "com.twitter" %% "inject-server" % "20.3.0" % "test" classifier "tests",
    "com.twitter" %% "inject-app" % "20.3.0" % "test" classifier "tests",
    "com.twitter" %% "inject-core" % "20.3.0" % "test" classifier "tests"
  )
}
