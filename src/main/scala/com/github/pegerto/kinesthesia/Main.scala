package com.github.pegerto.kinesthesia

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object Main extends App {
  implicit val system = ActorSystem("kinesthesia")

  val bindingFuture = Http().newServerAt("0.0.0.0", 8080)
    .bind(concat(RestApi.api, RestApi.staticResources))

  Await.result(bindingFuture, Duration.Inf)
}
