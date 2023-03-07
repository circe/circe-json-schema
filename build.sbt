ThisBuild / organization := "io.circe"
ThisBuild / githubWorkflowPublishTargetBranches := Nil
ThisBuild / githubWorkflowJobSetup := {
  (ThisBuild / githubWorkflowJobSetup).value.toList.map {
    case step @ WorkflowStep.Use(UseRef.Public("actions", "checkout", "v2"), _, _, _, _, _) =>
      step.copy(params = step.params.updated("submodules", "recursive"))
    case other => other
  }
}
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Use(
    UseRef.Public(
      "codecov",
      "codecov-action",
      "v1"
    )
  )
)

val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked"
)

val circeVersion = "0.14.5"
val everitVersion = "1.14.0"
val previousCirceJsonSchemaVersion = "0.1.0"

val scala212 = "2.12.17"
val scala213 = "2.13.10"
val scala3 = "3.2.2"

ThisBuild / crossScalaVersions := Seq(scala3, scala213, scala212)

val baseSettings = Seq(
  resolvers += "jitpack".at("https://jitpack.io"),
  scalacOptions ++= compilerOptions,
  scalacOptions ++= (
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Nil
      case Some((2, minor)) if minor >= 13 =>
        Seq(
          "-Ywarn-unused:imports",
          "-Ywarn-dead-code",
          "-Ywarn-numeric-widen"
        )
      case Some((2, minor)) if minor < 13 =>
        Seq(
          "-Xfuture",
          "-Yno-adapted-args",
          "-Ywarn-unused-import",
          "-Ywarn-dead-code",
          "-Ywarn-numeric-widen"
        )
      case _ => Nil
    }
  ),
  coverageHighlighting := true,
  (Compile / scalastyleSources) ++= (Compile / unmanagedSourceDirectories).value
)

val allSettings = baseSettings ++ publishSettings

val docMappingsApiDir = settingKey[String]("Subdirectory in site target directory for API docs")

val root = project
  .in(file("."))
  .settings(allSettings)
  .settings(
    noPublishSettings
  )
  .aggregate(schema)
  .dependsOn(schema)

lazy val schema = project
  .in(file("schema"))
  .settings(allSettings)
  .settings(
    moduleName := "circe-json-schema",
    mimaPreviousArtifacts := Set("io.circe" %% "circe-json-schema" % previousCirceJsonSchemaVersion),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion % Test,
      "io.circe" %% "circe-jawn" % circeVersion % Test,
      "io.circe" %% "circe-testing" % circeVersion % Test,
      "com.github.everit-org.json-schema" % "org.everit.json.schema" % everitVersion,
      "org.scalatest" %% "scalatest-flatspec" % "3.2.15" % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test
    ),
    ghpagesNoJekyll.withRank(KeyRanks.Invisible) := true,
    docMappingsApiDir := "api",
    addMappingsToSiteDir(Compile / packageDoc / mappings, docMappingsApiDir)
  )

lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/circe/circe-json-schema")),
  licenses := Seq("Apache 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots".at(nexus + "content/repositories/snapshots"))
    else
      Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
  },
  autoAPIMappings := true,
  apiURL := Some(url("https://circe.github.io/circe-json-schema/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/circe/circe-json-schema"),
      "scm:git:git@github.com:circe/circe-json-schema.git"
    )
  ),
  developers := List(
    Developer(
      "travisbrown",
      "Travis Brown",
      "travisrobertbrown@gmail.com",
      url("https://twitter.com/travisbrown")
    )
  )
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

credentials ++= (
  for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    username,
    password
  )
).toSeq

ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8")
// No auto-publish atm. Remove this line to generate publish stage
ThisBuild / githubWorkflowPublishTargetBranches := Seq.empty
ThisBuild / githubWorkflowJobSetup := {
  (ThisBuild / githubWorkflowJobSetup).value.toList.map {
    case step @ WorkflowStep.Use(UseRef.Public("actions", "checkout", "v2"), _, _, _, _, _) =>
      step.copy(params = step.params.updated("submodules", "recursive"))
    case other => other
  }
}
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(
    List("clean", "coverage", "test", "coverageReport", "scalastyle", "scalafmtCheckAll"),
    id = None,
    name = Some("Test")
  ),
  WorkflowStep.Use(
    UseRef.Public("codecov", "codecov-action", "e156083f13aff6830c92fc5faa23505779fbf649"), // v1.2.1
    name = Some("Upload code coverage")
  )
)
