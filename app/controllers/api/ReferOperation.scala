package controllers.api

import models.domain.serversetting.ServerSettingRepository
import models.infra.persistence.scalikejdbc.ScalikeServerSettingsRepository
import play.api.mvc.{Controller, Action}
import models.domain.Id
import play.api.libs.json.{JsObject, JsArray, JsString}
import models.domain.redis.RedisRepository

/**
 * @author kamekoopa
 */
object ReferOperation extends Controller {

  def serverSettingRepository: ServerSettingRepository = new ScalikeServerSettingsRepository()
  def redisRepository: RedisRepository = new RedisRepository()

  def keys(id: String) = Action {

    val maybeKeys = serverSettingRepository.find(Id(id)).map { server =>

      val keys = redisRepository.getAllKeys(server).map( key => JsString(key.name) )
        .foldLeft(JsArray()){ (a: JsArray, s: JsString) =>
          a append s
        }

      JsObject(Seq(
        "keys" -> keys
      ))
    }

    maybeKeys match {
      case Some(keys) => Ok(keys)
      case None => Ok(JsArray())
    }
  }

  def getRecord(id: String, key: String) = Action {

    val result = for(
      server <- serverSettingRepository.find(Id(id));
      record <- redisRepository.getRecord(server)(key).map( _.toJson )
    )yield record

    result match {
      case Some(r) => Ok(r)
      case None => NotFound(JsObject(Seq()))
    }
  }
}
