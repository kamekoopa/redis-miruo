package models.domain.redis

import redis.clients.jedis.Jedis
import play.api.libs.json._

/**
 * @author kamekoopa
 */
case class Record(key: Key, value: RedisValue, ttl: Long){

  val isNoExpiration: Boolean = ttl == -1
}

object Record {

  lazy val strArr: Array[String] = Array[String]()

  def apply(keyName: String)(implicit client: Jedis) = {
    import KeyType._
    import scala.collection.JavaConversions._

    Key(keyName).map({ key =>

      val ttl = client.ttl(key.name)

      val value = key.keyType match {
        case STRING => StringValue(client.get     (key.name         ))
        case LIST   => ListValue  (client.lrange  (key.name, 0L, -1L).toArray(strArr).toList)
        case SET    => SetValue   (client.smembers(key.name         ).toArray(strArr).toSet)
        case ZSET   => ZSetValue  (client.zrange  (key.name, 0L, -1L).toArray(strArr).toSet)
        case HASH   => HashValue  (client.hgetAll (key.name         ).toMap[String, String])
      }
      new Record(key, value, ttl)
    })
  }
}


import models.domain.redis.RedisValueWrites._

object RecordWrites {
  implicit object DefaultRecordWrites extends Writes[Record] {
    def writes(o: Record): JsValue = {
      Json.obj(
        "key"   -> JsString(o.key.name),
        "type"  -> JsString(o.key.keyType.keyType),
        "value" -> Json.toJson(o.value),
        "ttl"   -> JsNumber(o.ttl)
      )
    }
  }
}