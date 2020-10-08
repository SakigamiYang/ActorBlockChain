package me.sakigamiyang.actorblockchain.blockchain

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ChainSpec extends AnyFunSpec with Matchers {
  describe("A chain") {
    it("be correctly built") {
      val transaction = Transaction("me", "you", 1)

      val empty = Chain()
      empty.index shouldBe 0

      val link = ChainLink(1, List(transaction), 1, previousHash = "abc", tail = empty)
      val justOne = Chain(link)
      justOne.index shouldBe 1

      val link2 = ChainLink(2, List(transaction), 1, previousHash = "abc", tail = empty)
      val justTwo = link2 :: link

      justTwo.index shouldBe 2
    }
  }
}
