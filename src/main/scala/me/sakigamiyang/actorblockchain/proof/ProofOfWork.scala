package me.sakigamiyang.actorblockchain.proof

import me.sakigamiyang.actorblockchain.crypto.Crypto
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.annotation.tailrec

/**
 * PoW algorithm.
 */
object ProofOfWork {
  /**
   * Check Proof of Work from 0, repeat the algorithm increasing the proof by one when not valid.
   *
   * @param lastHash the hash of last block
   * @return valid proof
   */
  def proofOfWork(lastHash: String): Long = {
    @tailrec
    def powHelper(lastHash: String, proof: Long): Long = {
      if (validProof(lastHash, proof)) proof
      else powHelper(lastHash, proof + 1L)
    }

    val proof = 0L
    powHelper(lastHash, proof)
  }

  /**
   * Validate proof.
   *
   * @param lastHash the hash of last block
   * @param proof    proof
   * @return true if valid, false if not
   */
  def validProof(lastHash: String, proof: Long): Boolean = {
    val guess = (lastHash ++ proof.toString).toJson.toString
    val guessHash = Crypto.sha256Hash(guess)
    (guessHash take 4) == "0000"
  }
}
