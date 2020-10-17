package com.github.pegerto.kinesthesia

import akka.http.scaladsl.model.StatusCodes
import org.apache.kafka.clients.admin.AdminClient
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol
import scala.jdk.CollectionConverters._

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
    )
  }
}
