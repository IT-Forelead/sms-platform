package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.domain.SystemSetting.{UpdateSetting, UpdateTemplateOfBirthday}
import com.itforelead.smspaltfrom.domain.types.UserId
import com.itforelead.smspaltfrom.services.SystemSettings

class SystemSettingsStub[F[_]] extends SystemSettings[F] {
  override def settings(userId: UserId): F[Option[SystemSetting]]                     = ???
  override def update(userId: UserId, settings: UpdateSetting): F[SystemSetting]      = ???
  override def updateTemplateOfBirthday(userId: UserId, templates: UpdateTemplateOfBirthday): F[SystemSetting] = ???
}
