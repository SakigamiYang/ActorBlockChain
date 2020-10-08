package me.sakigamiyang.actorblockchain.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import me.sakigamiyang.actorblockchain.actor.BlockChain.{AddBlockCommand, GetChain, GetLastHash, GetLastIndex}
import me.sakigamiyang.actorblockchain.blockchain.{ChainLink, EmptyChain, Transaction}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class BlockChainSpec(sys: ActorSystem) extends TestKit(sys)
  with ImplicitSender
  with AnyFunSpecLike
  with Matchers
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("blockchain-test"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  describe("A Blockchain Actor") {
    it("correctly add a new block") {
      val blockchain = system.actorOf(BlockChain.props(EmptyChain, "test"))

      blockchain ! GetChain
      expectMsg(1000.millis, EmptyChain)

      blockchain ! GetLastIndex
      expectMsg(1000.millis, 0)

      blockchain ! GetLastHash
      expectMsg(1000.millis, "1")


      val transactions = List(Transaction("a", "b", 1L))
      val proof = 1L
      blockchain ! AddBlockCommand(transactions, proof, System.currentTimeMillis())
      expectMsg(1000.millis, 1)

      blockchain ! GetLastIndex
      expectMsg(1000.millis, 1)

      blockchain ! GetChain
      expectMsgType[ChainLink]
    }
  }

  describe("A Blockchain Actor") {
    it("correctly recover from a snapshot") {
      val blockchain = system.actorOf(BlockChain.props(EmptyChain, "test"))

      blockchain ! GetLastIndex
      expectMsg(1000.millis, 1)

      blockchain ! GetChain
      val ack = expectMsgType[ChainLink]

      ack.transactions.head.sender shouldBe "a"
    }
  }
}
