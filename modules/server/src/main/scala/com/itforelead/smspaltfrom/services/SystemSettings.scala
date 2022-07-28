package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.domain.SystemSetting.{UpdateSetting, UpdateTemplateOfBirthday}
import com.itforelead.smspaltfrom.domain.types.UserId
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.services.sql.SystemSettingSql.{select, updateSql, updateTemplatesSql}
import skunk.Session
import skunk.implicits.toIdOps

trait SystemSettings[F[_]] {

  /** Function for get the system settings
    *
    * @return
    *   list of system settings
    */
  def settings(userId: UserId): F[Option[SystemSetting]]

  /** Function for update the system settings
    *
    * @param settings
    *   of system settings
    * @return
    *   updated system setting
    */
  def update(userId: UserId, settings: UpdateSetting): F[SystemSetting]

  /** Function for update templates of birthday
    *
    * @param templates
    *   of system settings
    * @return
    *   updated system setting
    */
  def updateTemplateOfBirthday(userId: UserId, templates: UpdateTemplateOfBirthday): F[SystemSetting]

}

object SystemSettings {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): SystemSettings[F] =
    new SystemSettings[F] with SkunkHelper[F] {

      /** Function for get the system settings
        *
        * @return
        *   list of system settings
        */
      override def settings(userId: UserId): F[Option[SystemSetting]] =
        prepOptQuery(select, userId)

      /** Function for update the system settings
        *
        * @param settings
        *   of system settings
        * @return
        *   updated system setting
        */
      override def update(userId: UserId, settings: UpdateSetting): F[SystemSetting] =
        prepQueryUnique(updateSql, settings ~ userId)

      /** Function for update templates of birthday
        *
        * @param templates
        *   of system settings
        * @return
        *   updated system setting
        */
      override def updateTemplateOfBirthday(userId: UserId, templates: UpdateTemplateOfBirthday): F[SystemSetting] =
        prepQueryUnique(updateTemplatesSql, templates ~ userId)
    }
}
