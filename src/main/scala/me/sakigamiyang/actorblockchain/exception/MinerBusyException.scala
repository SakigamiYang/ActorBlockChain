package me.sakigamiyang.actorblockchain.exception

final class MinerBusyException(val message: String = "",
                               val cause: Throwable = None.orNull)
  extends Exception(message, cause)
