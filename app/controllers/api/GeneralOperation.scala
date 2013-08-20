package controllers.api

import play.api.mvc.{Controller, Action}
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import models.domain.serversetting.ServerSettingRepository
import models.infra.persistence.scalikejdbc.ScalikeServerSettingsRepository
import models.domain.Id
import models.domain.redis.{Informations, RedisRepository}

/**
 * @author kamekoopa
 */
object GeneralOperation extends Controller {

  def serverSettingRepository: ServerSettingRepository = new ScalikeServerSettingsRepository()
  def redisRepository: RedisRepository = new RedisRepository()

  def info(id: String) = Action {

    val maybeInfo:Option[Informations] = serverSettingRepository.find(Id(id)).map({ setting =>
      redisRepository.getInfo(setting)
    })

    val result = maybeInfo match {
      case Some(info) => {
        Ok(JsObject(Seq(
          "info" -> info.toJson
        )))
      }
      case None => {
        NotFound(JsObject(Seq(
          "error" -> JsString(s"id: $id setting not found")
        )))
      }
    }

    result
  }
}
