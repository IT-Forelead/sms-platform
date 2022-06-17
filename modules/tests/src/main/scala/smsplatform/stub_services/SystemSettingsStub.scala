package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.services.SystemSettings

class SystemSettingsStub[F[_]] extends SystemSettings[F] {
  override def settings: F[Option[SystemSetting]]                     = ???
  override def update(SystemSetting: SystemSetting): F[SystemSetting] = ???
}
