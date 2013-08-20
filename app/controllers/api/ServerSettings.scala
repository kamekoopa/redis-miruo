package controllers.api

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.domain.serversetting.{Input, ServerSettingRepository, ServerSetting}
import play.api.data.validation.ValidationError
import models.infra.persistence.scalikejdbc.ScalikeServerSettingsRepository
import models.domain.Id

/**
 * @author kamekoopa
 */
object ServerSettings extends Controller {

  def serverSettingRepository: ServerSettingRepository = new ScalikeServerSettingsRepository()

  def register = {
    Action(parse.json) { request =>

      val body = request.body

      val jsResult: JsResult[Input] = for(
        host <- (body \ "host").validate[String];
        port <- (body \ "port").validate[Int].orElse(JsResult.applicativeJsResult.pure(6379))
      ) yield {

        val pass = (body \ "password").asOpt[String]
        val desc = (body \ "description").asOpt[String]

        Input(host, port, pass, desc)
      }

      val response = jsResult match {
        case success: JsSuccess[Input] => {

          val created = serverSettingRepository.register(
            ServerSetting.newEntity(success.get)
          )

          created.fold({ th =>
            InternalServerError(JsObject(Seq(
              "error" -> JsString(th.getMessage)
            )))
          }, { setting =>
            Created(JsObject(Seq(
              "result" -> setting.json
            )))
          })
        }

        case JsError(errors) => {
          val jsErrors = toJsErrors(errors)

          BadRequest(JsObject(Seq(
            "error" -> jsErrors
          )))
        }
      }

      response
    }
  }

  def delete(id: String) = Action {

    val result = serverSettingRepository.delete(Id(id))

    if(result){
      NoContent
    }else{
      InternalServerError(JsObject(Seq(
        "error" -> JsString(s"id $id delete failed")
      )))
    }
  }

  def searchAll = {
    Action { request =>

      val results: JsArray = serverSettingRepository.searchAll()
        .map( _.json )
        .foldLeft(JsArray()){ (array, obj) =>
          array.append(obj)
        }

      Ok(results)
    }
  }

  private def toJsErrors(errors: Seq[( JsPath, Seq[ValidationError] )] ): JsArray = {

    errors.map({ pair: (JsPath, Seq[ValidationError]) =>

      val path = pair._1.toString()

      val validationErrors = pair._2
      val jsErrorMessages = validationErrors.map({ e: ValidationError =>
        JsString(e.message)
      })

      JsObject(Seq(
        path -> JsArray(jsErrorMessages)
      ))

    }).foldLeft(JsArray()){ (array, obj) =>
      array.append(obj)
    }
  }
}
