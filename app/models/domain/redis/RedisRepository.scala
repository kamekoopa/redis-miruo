package models.domain.redis

import redis.clients.jedis.Jedis
import models.domain.serversetting.ServerSetting
import scala.collection.JavaConversions

/**
 * @author kamekoopa
 */
class RedisRepository {

  def getClient(setting: ServerSetting) = {

    new Jedis(setting.getShardInfo)
  }

  def getClientWith[T](setting: ServerSetting)(process: (Jedis => T)) = {

    val client = getClient(setting)
    try{
      process(client)
    }finally{
      client.disconnect()
    }
  }

  def getInfo(setting: ServerSetting): Informations = {

    getClientWith(setting){ implicit client =>
      Informations(client.info().lines.toArray)
    }
  }

  def getAllKeys(setting: ServerSetting): Seq[Key] = {

    getClientWith(setting){ implicit client =>
      import JavaConversions._
      client.keys("*").map(Key(_)).collect({ case Some(key) => key }).toSeq
    }
  }

  def getRecord(setting: ServerSetting)(key: String):Option[Record] = {

    getClientWith(setting){ implicit client =>
      Record(key)
    }
  }
}
