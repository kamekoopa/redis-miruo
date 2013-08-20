import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "redis-miruo"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.apache.commons" % "commons-lang3" % "3.1",
    "org.sedis"          % "sedis_2.10.0"  % "1.1.8",
    "com.github.seratch" %% "scalikejdbc"               % "[1.6,)",
    "com.github.seratch" %% "scalikejdbc-interpolation" % "[1.6,)",
    "com.github.seratch" %% "scalikejdbc-play-plugin"   % "[1.6,)"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here

    resolvers += "Sedis" at "http://pk11-scratch.googlecode.com/svn/trunk",

    scalacOptions ++= Seq("-deprecation", "-feature")
  )
}
