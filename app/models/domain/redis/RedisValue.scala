package models.domain.redis

import play.api.libs.json.{JsObject, JsArray, JsString, JsValue}

/**
 * @author kamekoopa
 */
sealed abstract class RedisValue {

  val string: Option[String] = None
  val list: Option[List[String]] = None
  val set: Option[Set[String]] = None
  val zset: Option[Set[String]] = None
  val hash: Option[Map[String, String]] = None

  def toJson: JsValue
}

final case class StringValue(v: String) extends RedisValue {
  override val string: Option[String] = Some(v)

  def toJson = JsString(v)
}

final case class ListValue(v: List[String]) extends RedisValue {
  override val list: Option[List[String]] = Some(v)

  def toJson = JsArray(v.toSeq.map(JsString))
}

final case class SetValue(v: Set[String]) extends RedisValue {
  override val set: Option[Set[String]] = Some(v)

  def toJson = JsArray(v.toSeq.map(JsString))
}

final case class ZSetValue(v: Set[String]) extends RedisValue {
  override val zset: Option[Set[String]] = Some(v)

  def toJson = JsArray(v.toSeq.map(JsString))
}

final case class HashValue(v: Map[String, String]) extends RedisValue {
  override val hash: Option[Map[String, String]] = Some(v)

  def toJson = JsObject(v.toSeq.map( e => (e._1, JsString(e._2) )))
}

