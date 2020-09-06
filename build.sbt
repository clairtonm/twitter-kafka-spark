ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.10"

lazy val spark = (project in file("spark"))
  .settings(
    name := "Spark-Code",
    version := "0.1",

    libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.0",
    libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0" % "provided",
      libraryDependencies += "org.apache.spark" % "spark-streaming_2.12" % "3.0.0" % "provided"
  )

lazy val kafka = (project in file("kafka"))
  .settings(
    name := "Kafka-Code",
    version := "0.1",

    libraryDependencies += "com.danielasfregola" %% "twitter4s" % "6.2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0"

)