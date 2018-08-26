name := "funktor"

version := "1.0"

scalaVersion := "2.11.12"
val akkaVersion = "2.5.14"
val akkaHttpVersion = "10.1.4"
val alpakkaVersion = "0.16"
val sparkVersion = "2.3.1"

libraryDependencies ++= Seq(
  guice, ehcache, evolutions, jdbc,
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.13",
  "org.postgresql" % "postgresql" % "42.2.0",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.5",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.5",
  "org.apache.spark" %% "spark-core" % sparkVersion exclude("log4j", "slf4j-log4j12"),
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.hadoop" % "hadoop-client" % "3.1.0",
  "com.github.fommil.netlib" % "all" % "1.1.2",

  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "com.h2database" % "h2" % "1.4.196" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "net.jadler" % "jadler-all" % "1.3.0" % Test
  )

coverageExcludedPackages := "<empty>;views.*;Reverse.*;router.*;controllers.javascript;play.api.*"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
