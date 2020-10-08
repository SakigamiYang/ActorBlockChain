package me.sakigamiyang.actorblockchain

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.pubsub.DistributedPubSub
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import me.sakigamiyang.actorblockchain.actor.Node
import me.sakigamiyang.actorblockchain.api.NodeRoutes
import me.sakigamiyang.actorblockchain.cluster.ClusterManager

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Server extends App with NodeRoutes {
  implicit val system: ActorSystem = ActorSystem("ActorBlockChain")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config: Config = ConfigFactory.load()
  val address = config.getString("http.ip")
  val port = config.getInt("http.port")
  val nodeId = config.getString("actorblockchain.node.id")

  lazy val routes: Route = statusRoutes ~ transactionRoutes ~ mineRoutes

  val clusterManager: ActorRef = system.actorOf(ClusterManager.props(nodeId), "clusterManager")
  val mediator: ActorRef = DistributedPubSub(system).mediator
  val node: ActorRef = system.actorOf(Node.props(nodeId, mediator), "node")

  Http().newServerAt(address, port).bindFlow(routes)
  println(s"Server online at http://$address:$port/")

  Await.result(system.whenTerminated, Duration.Inf)
}
