package com.github.pegerto.kinesthesia

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import org.apache.kafka.clients.admin.{NewTopic, TopicDescription}
import spray.json.DefaultJsonProtocol._

import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

object RestApi {

  import KafkaAdminHelper._
  import Main._

  implicit val executionContext = system.dispatcher
  val client = getAdminClient()

  val staticResources =
    (get & pathPrefix("admin")) {
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromResource("admin/index.html")
      } ~ {
        getFromResourceDirectory("admin")
      }
    }

  val api = pathPrefix("v1") {
    concat(
      path("status") {
        get {
          val status = for {
            clusterId <- client.describeCluster().clusterId().asScala()
            nodes <- client.describeCluster().nodes().asScala()
            topics <- client.listTopics().names().asScala()
            clients <- client.listConsumerGroups().all().asScala()
          } yield Status(clusterId,
            nodes.size(),
            nodes.asScala.map(n => Node(n.id(), n.host(), n.port(), Option(n.rack()))).toList,
            topics.size(),
            clients.size())
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
              t => {
                val numPartitions = t.partitions().size()
                val numReplicas = t.partitions().get(0).replicas().size()
                val numInSync = t.partitions().asScala.map(_.isr().size()).sum
                TopicStatus(t.name(),
                  numPartitions,
                  numReplicas,
                  (numPartitions * numReplicas) - numInSync)
              })

            complete(topics)
          }
        )
      },
      path("partitions") {
        get {
          val partitionsPerNode: (Map[String, TopicDescription]) => Iterable[(Int, ReplicaStatus)] = tdMap => {
            for {
              td <- tdMap.values
              partition <- td.partitions().asScala.toList
              replica <- partition.replicas().asScala
            } yield (replica.id(), ReplicaStatus(
                    td.name(),
                    partition.partition(),
                    partition.leader() == replica,
                    !partition.isr().contains(replica)))
          }

          val partitions = for {
            topicNames <- client.listTopics().names().asScala()
            topics <- client.describeTopics(topicNames).all().asScala()
          } yield partitionsPerNode(topics.asScala.toMap)

          complete(partitions.map(_.groupMap(_._1.toString)(_._2)))
        }
      }
    )
  }
}
