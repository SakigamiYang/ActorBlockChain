package me.sakigamiyang.actorblockchain.actor

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.pubsub.DistributedPubSub
import akka.testkit.{ImplicitSender, TestKit}
import me.sakigamiyang.actorblockchain.actor.Broker.{AddTransaction, Clear, GetTransactions}
import me.sakigamiyang.actorblockchain.blockchain.Transaction
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class BrokerSpec(sys: ActorSystem) extends TestKit(sys)
  with ImplicitSender
  with AnyFunSpecLike
  with Matchers
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("broker-test"))

  val mediator: ActorRef = DistributedPubSub(this.system).mediator

  override def afterAll: Unit = {
    shutdown(system)
  }

  describe("A Broker Actor") {
    it("start with an empty list of transactions") {
      val broker = system.actorOf(Broker.props)

      broker ! GetTransactions
      expectMsg(500.millis, List())
    }
  }


  describe("A Broker Actor") {
    it("return the correct list of added transactions") {
      val broker = system.actorOf(Broker.props)
      val transaction1 = Transaction("A", "B", 100)
      val transaction2 = Transaction("C", "D", 1000)

      broker ! AddTransaction(transaction1)
      broker ! AddTransaction(transaction2)

      broker ! GetTransactions
      expectMsg(500.millis, List(transaction2, transaction1))
    }
  }


  describe("A Broker Actor") {
    it("clear the transaction lists when requested") {
      val broker = system.actorOf(Broker.props)
      val transaction1 = Transaction("A", "B", 100)
      val transaction2 = Transaction("C", "D", 1000)

      broker ! AddTransaction(transaction1)
      broker ! AddTransaction(transaction2)

      broker ! Clear

      broker ! GetTransactions
      expectMsg(500.millis, List())
    }
  }
}
