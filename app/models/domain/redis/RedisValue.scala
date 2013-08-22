package models.domain.redis

import play.api.libs.json._
import scala.Some

/**
 * @author kamekoopa
 */
sealed abstract class RedisValue {

  val string: Option[String] = None
  val list: Option[List[String]] = None
  val set: Option[Set[String]] = None
  val zset: Option[Set[String]] = None
  val hash: Option[Map[String, String]] = None
}

final case class StringValue(v: String) extends RedisValue {
  override val string: Option[String] = Some(v)
}

final case class ListValue(v: List[String]) extends RedisValue {
  override val list: Option[List[String]] = Some(v)
}

final case class SetValue(v: Set[String]) extends RedisValue {
  override val set: Option[Set[String]] = Some(v)
}

final case class ZSetValue(v: Set[String]) extends RedisValue {
  override val zset: Option[Set[String]] = Some(v)
}

final case class HashValue(v: Map[String, String]) extends RedisValue {
  override val hash: Option[Map[String, String]] = Some(v)
}

object RedisValueWrites{

  implicit object DefaultRedisValueWrites extends Writes[RedisValue]{
    def writes(o: RedisValue): JsValue = {
      o match {
        case StringValue(v) => Json.toJson(v)
        case ListValue(v)   => Json.toJson(v)
        case SetValue(v)    => Json.toJson(v)
        case ZSetValue(v)   => Json.toJson(v)
        case HashValue(v)   => Json.toJson(v)
      }
    }
  }
}