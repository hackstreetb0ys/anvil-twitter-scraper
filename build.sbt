name := """anvil-twitter-scraper"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws
  , "org.twitter4j" % "twitter4j-stream" % "4.0.4"
  , "com.rabbitmq" % "amqp-client" % "3.6.1"
  , "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

packageName in Universal := "dist"