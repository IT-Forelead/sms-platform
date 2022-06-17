package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.services.sql.SystemSettingSql.{select, updateSql}
import skunk.{Session, Void}

trait SystemSettings[F[_]] {

  /** Function for get the system settings
    *
    * @return
    *   list of system settings
    */
  def settings: F[Option[SystemSetting]]

  /** Function for update the system settings
    *
    * @param settings
    *   of system settings
    * @return
    *   updated system setting
    */
  def update(settings: SystemSetting): F[SystemSetting]

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
      override def settings: F[Option[SystemSetting]] =
        prepOptQuery(select, Void)

      /** Function for update the system settings
        *
        * @param settings
        *   of system settings
        * @return
        *   updated system setting
        */
      override def update(settings: SystemSetting): F[SystemSetting] =
        prepQueryUnique(updateSql, settings)
    }
}
