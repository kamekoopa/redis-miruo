package models.infra.persistence.scalikejdbc

import models.utils.TimeProvider.NowProvider
import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import models.domain.serversetting._
import models.domain.Id

/**
 * @author kamekoopa
 */
class ScalikeServerSettingsRepository(
  implicit private val session: DBSession = AutoSession,
  implicit private val nowProvider: NowProvider
) extends ServerSettingRepository {

  lazy val s = ServerSetting.syntax("s")

  def register(setting: ServerSetting) = {

    val now = Some(nowProvider.now())

    val result: Boolean = withSQL {
      insert.into(ServerSetting).values(
        setting.identifier.get,
        setting.host,
        setting.port,
        setting.password,
        setting.description,
        now,
        now
      )
    }.update().apply() == 1

    if(result){
      Right(setting.copy(
        createdAt = now,
        updatedAt = now
      ))
    }else{
      Left(new Exception("setting create failure"))
    }
  }

  def find(id: Id): Option[ServerSetting] = withSQL {
      select
        .from(ServerSetting as s)
        .where
          .eq(s.id, id.get)
    }.map(ServerSetting(s.resultName))
    .single()
    .toOption()
    .apply()

  def searchAll(): List[ServerSetting] = withSQL {
      select
        .from(ServerSetting as s)
        .orderBy(s.createdAt)
    }
    .map(ServerSetting(s.resultName))
    .list()
    .apply()

  def delete(id: Id): Boolean = {
    withSQL {
      deleteFrom(ServerSetting as s)
        .where
          .eq(s.id, id.get)
    }.update().apply() == 1
  }
}
