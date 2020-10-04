package me.sakigamiyang.actorblockchain.blockchain

import java.security.InvalidParameterException

import me.sakigamiyang.actorblockchain.crypto.Crypto
import me.sakigamiyang.actorblockchain.utils.JsonSupport._


/**
 * The core of our blockchain: it is a linked list of blocks containing transactions.
 */
sealed trait Chain {
  val index: Int
  val hash: String
  val transactions: List[Transaction]
  val proof: Long
  val timestamp: Long

  /**
   * Define addition operator `::` inside the [[Chain]] trait lick action of [[List]].
   *
   * @param link a new block
   * @return a new [[ChainLink]] adding as a tail
   */
  def ::(link: Chain): Chain = link match {
    case l: ChainLink => ChainLink(l.index, l.transactions, l.proof, l.timestamp, this.hash, this)
    case _ => throw new InvalidParameterException("Cannot add invalid link to chain")
  }
}

/**
 * Companion object that defines an `apply` method to create a new chain passing it a list of blocks.
 */
object Chain {
  /**
   * Create new [[ChainLink]] adding as a tail.
   *
   * @param b blocks
   * @return [[EmptyChain]] if b has no element, else [[ChainLink]]
   */
  def apply(b: Chain*): Chain = {
    if (b.isEmpty) EmptyChain
    else {
      val link = b.head.asInstanceOf[ChainLink]
      ChainLink(link.index, link.transactions, link.proof, link.timestamp, link.previousHash, apply(b.tail: _*))
    }
  }
}

/**
 * A linked list of blocks containing a list of transactions.
 *
 * @param index        index
 * @param transactions list of [[Transaction]]
 * @param proof        PoW
 * @param timestamp    timestamp of the block creation
 * @param previousHash hash value of previous block
 * @param tail         next block
 */
case class ChainLink(index: Int,
                     transactions: List[Transaction],
                     proof: Long,
                     timestamp: Long = System.currentTimeMillis(),
                     previousHash: String = "",
                     tail: Chain = EmptyChain) extends Chain {
  val hash: String = Crypto.sha256Hash(ChainLinkJsonFormat.write(this).toString)
}

/**
 * An empty chain only with a header, and it is implemented as a singleton.
 */
case object EmptyChain extends Chain {
  override val index = 0
  override val hash = "1" // the hash value is set to a default one in the EmptyChain
  override val transactions: List[Transaction] = Nil
  override val proof = 100L
  override val timestamp: Long = System.currentTimeMillis()
}
