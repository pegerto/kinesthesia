name := "kinesthesia"

version := "0.1"

scalaVersion := "2.13.3"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.1"
libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.apache.kafka" % "kafka-clients" % "2.5.1"
)

