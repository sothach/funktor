name := "funktor"

version := "1.0"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  ehcache,
  evolutions,
  "org.playframework.anorm" %% "anorm" % "2.6.2",
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.13",
  "org.postgresql" % "postgresql" % "42.2.0",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.5",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.5",
  "org.apache.spark" %% "spark-core" % "2.2.1" exclude("log4j", "slf4j-log4j12"),
  "org.apache.spark" %% "spark-sql" % "2.2.1",
  "org.apache.spark" %% "spark-mllib" % "2.2.1",
  "org.apache.hadoop" % "hadoop-client" % "3.1.0",
  "com.github.fommil.netlib" % "all" % "1.1.2",
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.2.0"
  )     

lazy val root = (project in file(".")).enablePlugins(PlayScala)
