package models.domain.redis

import play.api.libs.json.{JsString, Json, JsValue, Writes}

/**
 * @author kamekoopa
 */
sealed abstract class Information
final case class Content(key: String, value: String) extends Information
final case class Comment(comment: String) extends Information

object Information {
  def apply(line: String) = {
    if(line.startsWith("#")){
      Some(Comment(line.replaceAll("# *", "").trim))
    }else if(line.length == 0){
      None
    }else{
      val splits = line.split(":", 2)
      Some(Content(splits(0).trim, splits(1).trim))
    }
  }
}

object InformationWrites {
  implicit object DefaultInformationWrites extends Writes[Information]{
    def writes(o: Information): JsValue = {
      o match {
        case Content(k, v) => {
          Json.obj(
            "type" -> JsString("content"),
            "body" -> Json.obj("key" -> JsString(k), "value" -> JsString(v))
          )
        }
        case Comment(c) => {
          Json.obj(
            "type" -> JsString("content"),
            "body" -> Json.obj("comment" -> JsString(c))
          )
        }
      }
    }
  }
}

