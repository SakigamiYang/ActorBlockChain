package me.sakigamiyang.actorblockchain.blockchain

/**
 * Transactions register the movement of coins between two entities.
 *
 * @param sender    sender
 * @param recipient recipient
 * @param amount    amount
 */
case class Transaction(sender: String, recipient: String, amount: Long)
