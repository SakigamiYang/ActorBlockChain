package me.sakigamiyang.actorblockchain.actor

import akka.actor.{Actor, ActorLogging, Props}
import me.sakigamiyang.actorblockchain.blockchain.Transaction

/**
 * The manager of the transactions of our blockchain.
 * Its responsibilities are the addition of new transactions,
 * and the retrieval of pending ones.
 */
class Broker extends Actor with ActorLogging {

  import Broker._

  var pending: List[Transaction] = List()

  override def receive: Receive = {
    case AddTransaction(transaction) =>
      pending = transaction :: pending
      log.info(s"Added $transaction to pending Transaction")
    case GetTransactions =>
      log.info("Getting pending transactions")
      sender() ! pending
    case Clear =>
      pending = List()
      log.info("Clear pending transaction List")
  }
}

object Broker {

  sealed trait BrokerMessage

  case class AddTransaction(transaction: Transaction) extends BrokerMessage

  case object GetTransactions extends BrokerMessage

  case object Clear extends BrokerMessage

  val props: Props = Props(new Broker)
}
