package me.sakigamiyang.actorblockchain.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProofOfWorkSpec extends AnyFunSpec with Matchers {
  describe("Validation of proof") {
    it("correctly validate proofs") {
      val lastHash = "1"
      val correctProof = 7178
      val wrongProof = 0
      ProofOfWork.validProof(lastHash, correctProof) shouldBe true
      ProofOfWork.validProof(lastHash, wrongProof) shouldBe false
    }
  }
}
