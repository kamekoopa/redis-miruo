package models.domain.redis

import play.api.libs.json._
import scala.Some

/**
 * @author kamekoopa
 */
class Informations(val informations: Seq[Information])
object Informations {
  import scala.language.implicitConversions
  implicit def informationsToSeq(informations: Informations) = informations.informations

  def apply(lines: Array[String]) = {

    val infos:Seq[Information] = lines
      .map(line => Information(line))
      .collect({ case Some(i) => i })
      .toSeq

    new Informations(infos)
  }
}

import models.domain.redis.InformationWrites._

object InformationsWrites {
  implicit object DefaultInformationsWrites extends Writes[Informations]{
    def writes(o: Informations): JsValue = {

      val jsons = o.map ({ i: Information =>
        Json.toJson(i)(DefaultInformationWrites)
      })

      Json.toJson(jsons)
    }
  }
}
