package com.github.pegerto.kinesthesia

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._
import scala.util.Success

object RestApi {

  import KafkaAdminHelper._
  import Main._

  implicit val executionContext = system.dispatcher
  val client = AdminClient.create(Map[String, Object](
    "bootstrap.servers" -> "localhost:9093").asJava)

  val staticResources =
    (get & pathPrefix("admin")){
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromResource("admin/index.html")
      } ~ {
        getFromResourceDirectory("admin")
      }
    }

  case class Node(id: Int, host: String, port: Int, rack: Option[String])
  case object Node extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val nodeFormat = jsonFormat4(Node.apply)
  }

  case class Status(clusterId: String, nodeCount: Int, nodes: List[Node])
  case object Status extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val statusFormat = jsonFormat3(Status.apply)
  }

  case class Topic(name: String, partitionNumber: Option[Int], replicationFactor: Option[Int])
  case object Topic extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val topicFormat = jsonFormat3(Topic.apply)
  }

  val api = pathPrefix("v1") {
    concat(
      path("status") {
        get {
          val status = for {
            clusterId <- client.describeCluster().clusterId().asScala()
            nodes <- client.describeCluster().nodes().asScala()
          } yield  Status(clusterId,
            nodes.size(),
            nodes.asScala.map(n => Node(n.id(), n.host(), n.port(), Option(n.rack()))).toList)
          complete(status)
        }
      },
      path("topics") {
        concat(
          post {
            entity(as[Topic]) { topic =>
              val newTopic = new NewTopic(topic.name,
                topic.partitionNumber.map(Integer.valueOf(_)).toJava,
                topic.replicationFactor.map(r => Short.box(r.toShort)).toJava)
              client.createTopics(List(newTopic).asJava).all().asScala()
              complete(topic)
            }
          },
          get {
              val topics = for {
                topicNames <- client.listTopics().names().asScala()
                topics <- client.describeTopics(topicNames).all().asScala()
              } yield topics.values().asScala.map(
                t => Topic(t.name(),
                  Some(t.partitions().size()),
                  Some(t.partitions().get(0).replicas().size())))

            complete(topics)}
        )
      },
    )
  }
}
