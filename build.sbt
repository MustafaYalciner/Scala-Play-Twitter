name := """Twitter"""
organization := "com.tu"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += jdbc
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"
libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"
)
//libraryDependencies ++= Seq(
//  jdbc,
//  "org.playframework.anorm" %% "anorm" % "2.6.2"
//)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.tu.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.tu.binders._"
