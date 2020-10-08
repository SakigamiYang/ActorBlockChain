package me.sakigamiyang.actorblockchain.cluster

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class ClusterListener(nodeId: String, cluster: Cluster) extends Actor with ActorLogging {
  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = initialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info(s"Node $nodeId - Member is Up: ${member.address}")
    case UnreachableMember(member) =>
      log.info(s"Node $nodeId - Member detected as unreachable: $member")
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Node $nodeId - Member is Removed: ${member.address} after $previousStatus")
    case _: MemberEvent => // ignore
  }
}

object ClusterListener {
  def props(nodeId: String, cluster: Cluster): Props = Props(new ClusterListener(nodeId, cluster))
}
