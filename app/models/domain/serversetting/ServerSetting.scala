package models.domain.serversetting

import models.domain.{Id, Entity}
import java.util.Date
import play.api.libs.json.{JsNumber, JsString, JsObject}
import scalikejdbc.SQLInterpolation._
import scalikejdbc.WrappedResultSet
import redis.clients.jedis.JedisShardInfo

/**
 * @author kamekoopa
 */
case class ServerSetting private (
  id: Id = Id.generate,
  host: String,
  port: Int = 6379,
  password: Option[String] = None,
  description: Option[String] = None,
  createdAt: Option[Date] = None,
  updatedAt: Option[Date] = None
) extends Entity[ServerSetting]{

  val identifier = id

  def getShardInfo = {

    val shardInfo = password.fold(new JedisShardInfo(host, port)){ pass =>
      val shardInfo = new JedisShardInfo(host, port)
      shardInfo.setPassword(pass)
      shardInfo
    }
    shardInfo.setTimeout(10 * 1000)
    shardInfo
  }

  def json = {
    JsObject(Seq(
      "id"          -> JsString(identifier.get),
      "host"        -> JsString(host),
      "port"        -> JsNumber(port),
      "password"    -> JsString(password.getOrElse("")),
      "description" -> JsString(description.getOrElse(""))
    ))
  }
}

object ServerSetting extends SQLSyntaxSupport[ServerSetting] {

  override def tableName = "server_settings"

  //override def columns: Seq[String] = Seq("id", "host", "port", "password", "description", "created_at", "updated_at")

  def apply(s: ResultName[ServerSetting])(rs: WrappedResultSet): ServerSetting = {
    ServerSetting(
      id = Id(rs.string(s.id)),
      host = rs.string(s.host),
      port = rs.int(s.port),
      password = rs.stringOpt(s.password),
      description = rs.stringOpt(s.description),
      createdAt = rs.dateOpt(s.createdAt),
      updatedAt = rs.dateOpt(s.updatedAt)
    )
  }

  def newEntity (
    input: Input
  ) = {
    ServerSetting(
      host = input.host,
      port = input.port,
      password = input.pass,
      description = input.desc,
      createdAt = None,
      updatedAt = None
    )
  }
}

case class Input(host: String, port: Int, pass: Option[String], desc: Option[String])