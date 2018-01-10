import scalariform.formatter.preferences._

name := """play-rms"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator
resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
    "com.typesafe.play" % "play-slick_2.11" % "2.1.0",
    "com.typesafe.play" % "play-slick-evolutions_2.11" % "2.1.0",
    "mysql" % "mysql-connector-java" % "5.1.34",
    "org.webjars" % "coffee-script" % "1.11.0",
    "org.webjars" % "bootstrap" % "3.3.6",
    "org.webjars" % "startbootstrap-sb-admin-2" % "3.3.7+1",
    "org.webjars" % "less" % "2.5.3",
    "com.typesafe.play" %% "play-mailer" % "5.0.0",
    "org.webjars" % "font-awesome" % "4.3.0",
    "org.webjars" % "metisMenu" % "1.1.3",
    "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
    "com.h2database" % "h2" % "1.4.188",
    "net.codingwell" %% "scala-guice" % "4.0.0",
    "com.mohiva" %% "play-silhouette" % "3.0.2",
    "net.ceedubs" %% "ficus" % "1.1.2",
    "com.mohiva" %% "play-silhouette-testkit" % "3.0.2" % "test",
    "org.webjars" %% "webjars-play" % "2.4.0-1",
    specs2 % Test,
    cache,
    ws,
    evolutions,
    filters

//  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
//  specs2 % Test
)

scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xlint", // Enable recommended additional warnings.
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
    "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
  .setPreference(PreserveDanglingCloseParenthesis, true)
//fork in run := true