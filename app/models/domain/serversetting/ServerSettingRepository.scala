package models.domain.serversetting

import models.domain.Id


/**
 * @author kamekoopa
 */
trait ServerSettingRepository {

  def register(server: ServerSetting): Either[Throwable, ServerSetting]

  def find(id: Id): Option[ServerSetting]

  def searchAll(): List[ServerSetting]

  def delete(id: Id): Boolean
}
