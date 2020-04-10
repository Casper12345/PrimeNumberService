package com.prime.serializer

import com.server.PrimeList
import spray.json._

object JsonSerializers {

  implicit val PrimeListWriter: JsonWriter[PrimeList] = new JsonWriter[PrimeList] {
    override def write(obj: PrimeList): JsValue = obj match {
      case PrimeList(xs) => JsObject("prime_list" -> JsArray(xs.map(JsNumber(_)).toVector))
      case _ => JsObject("prime_list" -> JsArray(Vector.empty[JsNumber]))
    }
  }

}
