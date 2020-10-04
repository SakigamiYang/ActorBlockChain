package me.sakigamiyang.actorblockchain.crypto

import java.math.BigInteger
import java.security.MessageDigest

/**
 * Crypto utilities.
 */
object Crypto {
  /**
   * Calculate SHA-256 hash value.
   *
   * @param value value to hash
   * @return hash value
   */
  def sha256Hash(value: String): String =
    String.format("%064x", new BigInteger(1,
      MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"))))
}
