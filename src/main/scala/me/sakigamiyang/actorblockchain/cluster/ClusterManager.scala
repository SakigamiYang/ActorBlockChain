package me.sakigamiyang.actorblockchain.cluster

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.{Cluster, MemberStatus}

class ClusterManager(nodeId: String) extends Actor with ActorLogging {

  import ClusterManager._

  val cluster: Cluster = Cluster(context.system)
  val listener: ActorRef = context.actorOf(ClusterListener.props(nodeId, cluster), "clusterListener")

  override def receive: Receive = {
    case GetMembers =>
      sender() ! cluster.state.members
        .filter(_.status == MemberStatus.up)
        .map(_.address.toString)
        .toList
  }
}

object ClusterManager {

  sealed trait ClusterMessage

  case object GetMembers extends ClusterMessage

  def props(nodeId: String): Props = Props(new ClusterManager(nodeId))
}
