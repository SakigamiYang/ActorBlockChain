package me.sakigamiyang.actorblockchain.utils

import me.sakigamiyang.actorblockchain.blockchain._
import spray.json._


object JsonSupport extends DefaultJsonProtocol {

  implicit object TransactionJsonFormat extends RootJsonFormat[Transaction] {

    override def read(json: JsValue): Transaction =
      json.asJsObject.getFields("sender", "recipient", "amount") match {
        case Seq(JsString(sender), JsString(recipient), JsNumber(amount)) =>
          Transaction(sender, recipient, amount.toLong)
        case _ => throw DeserializationException("Cannot deserialize: Transaction expected")
      }

    override def write(obj: Transaction): JsValue = JsObject(
      "sender" -> JsString(obj.sender),
      "recipient" -> JsString(obj.recipient),
      "amount" -> JsNumber(obj.amount),
    )
  }

  implicit object ChainLinkJsonFormat extends RootJsonFormat[ChainLink] {

    override def read(json: JsValue): ChainLink =
      json.asJsObject.getFields("index", "transactions", "proof", "timestamp", "previousHash", "tail") match {
        case Seq(JsNumber(index), transactions, JsNumber(proof), JsNumber(timestamp), JsString(previousHash), tail) =>
          ChainLink(index.toInt, transactions.convertTo[List[Transaction]], proof.toLong,
            timestamp.toLong, previousHash, tail.convertTo(ChainJsonFormat))
        case _ => throw DeserializationException("Cannot deserialize: ChainLink expected")
      }

    override def write(obj: ChainLink): JsValue = JsObject(
      "index" -> JsNumber(obj.index),
      "proof" -> JsNumber(obj.proof),
      "transactions" -> JsArray(obj.transactions.map(_.toJson).toVector),
      "previousHash" -> JsString(obj.previousHash),
      "timestamp" -> JsNumber(obj.timestamp),
      "tail" -> obj.tail.toJson,
    )
  }

  implicit object ChainJsonFormat extends RootJsonFormat[Chain] {
    override def read(json: JsValue): Chain =
      json.asJsObject.getFields("previousHash") match {
        case Seq(_) => json.convertTo[ChainLink]
        case Seq() => EmptyChain
      }

    override def write(obj: Chain): JsValue = obj match {
      case link: ChainLink => link.toJson
      case EmptyChain => JsObject(
        "index" -> JsNumber(EmptyChain.index),
        "hash" -> JsString(EmptyChain.hash),
        "values" -> JsArray(),
        "proof" -> JsNumber(EmptyChain.proof),
        "timeStamp" -> JsNumber(EmptyChain.timestamp)
      )
    }
  }

}
