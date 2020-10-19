package com.github.pegerto.kinesthesia

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.common.KafkaFuture

import scala.concurrent.{Future, Promise}
import scala.jdk.CollectionConverters._

object KafkaAdminHelper{
  val conf: Config = ConfigFactory.load
  implicit class KafkaFutureWrapper[T](kf: KafkaFuture[T]) {
    def asScala(): Future[T] = {
      val promise = Promise[T]()
      kf.whenComplete((t: T, e: Throwable) => {
        if (t != null)  promise.success(t) else promise.failure(e)
        })
      promise.future
    }
  }

  def getAdminClient(): AdminClient = {
    val kafkaConfig = conf.getConfig("kafka")

    AdminClient.create(Map[String, Object](
      "bootstrap.servers" -> kafkaConfig.getString("brokers")).asJava)
  }

}
