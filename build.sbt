val zioVersion             = "1.0.4"
val courierVersion         = "3.0.0-RC1"
val mockJavamailVersion    = "1.9"
val organizeImportsVersion = "0.4.4"

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / scalacOptions += "-Wunused:imports"

ThisBuild / organization := "dev.doamaral"
ThisBuild / organizationName := "doamaral"
ThisBuild / organizationHomepage := Some(url("https://www.dylan.doamaral.dev/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/dylandoamaral/zemail"),
    "scm:git@github.com:dylandoamaral/zemail.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "ddoamaral",
    name = "Dylan DO AMARAL",
    email = "do.amaral.dylan@gmail.com",
    url = url("https://www.dylan.doamaral.dev/")
  )
)

ThisBuild / description := "A ZIO wrapper to send Mail."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/dylandoamaral/zemail"))

ThisBuild / coverageExcludedPackages := ".*zemail.example.*"

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches ++=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use("olafurpg", "setup-gpg", "v3")

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

ThisBuild / githubWorkflowBuildPreamble := Seq(
  WorkflowStep.Sbt(
    name = Some("Check formatting"),
    commands = List("scalafmtCheck")
  ),
  WorkflowStep.Sbt(
    name = Some("Check linting"),
    commands = List("scalafix --check")
  )
)
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("coverage", "test")))
ThisBuild / githubWorkflowBuildPostamble := Seq(
  WorkflowStep.Run(
    name = Some("Generate coverage"),
    commands = List("sbt coverageReport && bash <(curl -s https://codecov.io/bash)")
  )
)

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % organizeImportsVersion

lazy val root = (project in file("."))
  .settings(
    name := "zemail",
    libraryDependencies ++= Seq(
      "dev.zio"                %% "zio"           % zioVersion,
      "dev.zio"                %% "zio-test"      % zioVersion          % "test",
      "dev.zio"                %% "zio-test-sbt"  % zioVersion          % "test",
      "com.github.daddykotex"  %% "courier"       % courierVersion,
      "org.jvnet.mock-javamail" % "mock-javamail" % mockJavamailVersion % "test"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
