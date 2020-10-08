package me.sakigamiyang.actorblockchain.actor

import akka.actor.{ActorLogging, Props}
import akka.persistence._
import me.sakigamiyang.actorblockchain.blockchain.{Chain, ChainLink, Transaction}

/**
 * Actor interacts with the business logic of the blockchain.
 * It can add a new block to the blockchain,
 * and it can retrieve information about the state of the blockchain.
 * This actor has another superpower:
 * it can persist and recover the state of the blockchain.
 * This is possible implementing the [[PersistentActor]] trait
 * provided by the Akka Framework.
 */
class BlockChain(chain: Chain, nodeId: String) extends PersistentActor with ActorLogging {

  import BlockChain._

  var state: State = State(chain)

  override def persistenceId: String = s"chainer-$nodeId"

  def updateState(event: BlockChainEvent): Unit = event match {
    case AddBlockEvent(transactions, proof, timestamp) =>
      state = State(ChainLink(state.chain.index + 1, transactions, proof, timestamp) :: state.chain)
      log.info(s"Added block ${state.chain.index} containing ${transactions.size} transactions")
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(metadata, snapshot: State) =>
      log.info(s"Recovering from snapshot ${metadata.sequenceNr} at block ${snapshot.chain.index}")
      state = snapshot
    case RecoveryCompleted => log.info("Recovery completed")
    case event: AddBlockEvent => updateState(event)
  }

  override def receiveCommand: Receive = {
    case SaveSnapshotSuccess(metadata) =>
      log.info(s"Snapshot ${metadata.sequenceNr} saved successfully")
    case SaveSnapshotFailure(metadata, reason) =>
      log.error(s"Error saving snapshot ${metadata.sequenceNr}: ${reason.getMessage}")
    case AddBlockCommand(transactions: List[Transaction], proof: Long, timestamp: Long) =>
      persist(AddBlockEvent(transactions, proof, timestamp)) { event =>
        updateState(event)
      }
      // This is a workaround to wait until the state is persisted
      deferAsync(Nil) { _ =>
        saveSnapshot(state)
        sender() ! state.chain.index
      }
    case AddBlockCommand(_, _, _) =>
      log.error("invalid add block command")
    case GetChain =>
      sender() ! state.chain
    case GetLastHash =>
      sender() ! state.chain.hash
    case GetLastIndex =>
      sender() ! state.chain.index
  }
}

object BlockChain {

  sealed trait BlockChainEvent

  case class AddBlockEvent(transactions: List[Transaction],
                           proof: Long,
                           timestamp: Long) extends BlockChainEvent

  sealed trait BlockChainCommand

  case class AddBlockCommand(transactions: List[Transaction],
                             proof: Long,
                             timestamp: Long) extends BlockChainCommand

  case object GetChain extends BlockChainCommand

  case object GetLastHash extends BlockChainCommand

  case object GetLastIndex extends BlockChainCommand

  case class State(chain: Chain)

  def props(chain: Chain, nodeId: String): Props = Props(new BlockChain(chain, nodeId))
}
