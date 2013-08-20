package models.domain.redis

import redis.clients.jedis.Jedis

/**
 * @author kamekoopa
 */
case class Key(name: String, keyType: KeyType)
object Key {

  def apply(keyName: String)(implicit client: Jedis): Option[Key] = {

    KeyType.of(client.`type`(keyName)).map { keyType =>
      Key(keyName, keyType)
    }
  }
}


sealed abstract class KeyType(val keyType: String)
object KeyType {

  case object STRING extends KeyType("string")
  case object LIST extends KeyType("list")
  case object SET extends KeyType("set")
  case object ZSET extends KeyType("zset")
  case object HASH extends KeyType("hash")


  val values: Array[KeyType] = Array(STRING, LIST, SET, ZSET, HASH)

  def of(typeName: String) = values.find(_.keyType == typeName)
}
