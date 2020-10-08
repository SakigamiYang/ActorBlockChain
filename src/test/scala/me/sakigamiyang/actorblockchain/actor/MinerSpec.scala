package me.sakigamiyang.actorblockchain.actor

import akka.actor.ActorSystem
import akka.actor.Status.{Failure, Success}
import akka.testkit.{ImplicitSender, TestKit}
import me.sakigamiyang.actorblockchain.actor.Miner.{Mine, Ready}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
import scala.concurrent.duration._

class MinerSpec(sys: ActorSystem) extends TestKit(sys)
  with ImplicitSender
  with AnyFunSpecLike
  with Matchers
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("miner-test"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  describe("A Miner Actor") {
    it("be ready when requested") {
      val miner = system.actorOf(Miner.props)
      miner ! Ready
      miner ! Ready
      expectMsg(500.millis, Success("OK"))
    }
  }

  describe("A Miner Actor") {
    it("be busy while mining a new block") {
      val miner = system.actorOf(Miner.props)
      miner ! Ready
      miner ! Mine("1")
      expectMsgClass(500.millis, classOf[Future[Long]])
      miner ! Mine("2")
      expectMsgType[Failure]
    }
  }
}
