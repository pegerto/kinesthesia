package com.github.pegerto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

package object kinesthesia {
  case class Node(id: Int, host: String, port: Int, rack: Option[String])
  case object Node extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val nodeFormat = jsonFormat4(Node.apply)
  }

  case class Status(clusterId: String, nodeCount: Int, nodes: List[Node], topicCount: Int, clientCount: Int)
  case object Status extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val statusFormat = jsonFormat5(Status.apply)
  }

  case class Topic(name: String, partitionNumber: Option[Int], replicationFactor: Option[Int])
  case object Topic extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val topicFormat = jsonFormat3(Topic.apply)
  }
  case class TopicStatus(name: String,
                         partitionNumber: Int,
                         replicationFactor: Int,
                         underReplicatedPartitions: Int)
  case object TopicStatus extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val topicStatusFormat = jsonFormat4(TopicStatus.apply)
  }

}
