package com.github.pegerto.kinesthesia

import org.apache.kafka.common.KafkaFuture

import scala.concurrent.{Future, Promise}

object KafkaAdminHelper{

  implicit class KafkaFutureWrapper[T](kf: KafkaFuture[T]) {
    def asScala(): Future[T] = {
      val promise = Promise[T]()
      kf.whenComplete((t: T, e: Throwable) => {
        if (t != null)  promise.success(t) else promise.failure(e)
        })
      promise.future
    }
  }

}
