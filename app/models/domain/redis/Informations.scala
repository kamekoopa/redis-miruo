package models.domain.redis

import play.api.libs.json.{JsArray, JsString, JsObject, JsValue}

/**
 * @author kamekoopa
 */
class Informations(val informations: Seq[Information]){

  def toJson = {
    informations.foldLeft(JsArray())({ (a, b) =>
      a.append(b.toJson)
    })
  }
}
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

sealed abstract class Information {
  def toJson:JsValue
}
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

final case class Content(key: String, value: String) extends Information{

  def toJson = {
    JsObject(Seq(
      "type"-> JsString("content"),
      "body" -> JsObject(Seq(
        "key" -> JsString(key),
        "value" -> JsString(value)
      ))
    ))
  }
}

final case class Comment(comment: String) extends Information{

  def toJson = {
    JsObject(Seq(
      "type" -> JsString("comment"),
      "body" -> JsObject(Seq(
        "comment" -> JsString(comment)
      ))
    ))
  }
}
