name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
    "com.typesafe.play" % "play-slick_2.11" % "2.1.0",
    "com.typesafe.play" % "play-slick-evolutions_2.11" % "2.1.0",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
    "mysql" % "mysql-connector-java" % "5.1.34",
    "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
    "org.webjars" % "coffee-script" % "1.11.0",
    "org.webjars" % "less" % "2.5.3",
    "com.nulab-inc" %% "play2-oauth2-provider" % "0.17.0",
    specs2 % Test
)

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

fork in run := true