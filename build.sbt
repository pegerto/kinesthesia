name := "kinesthesia"

scalaVersion := "2.13.3"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.1"

publish / skip := true
enablePlugins(sbtdocker.DockerPlugin, JavaAppPackaging)
dockerExposedPorts ++= Seq(8080)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.apache.kafka" % "kafka-clients" % "2.5.1"
)

