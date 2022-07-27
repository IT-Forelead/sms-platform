package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.types.{TemplateId, UserId}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder, show)
case class SystemSetting(
  userId: UserId,
  autoSendBirthday: Boolean,
  autoSendHoliday: Boolean,
  smsMenId: Option[TemplateId] = None,
  smsWomenId: Option[TemplateId] = None
)

object SystemSetting {
  @derive(decoder, encoder, show)
  case class UpdateSetting(
    autoSendBirthday: Boolean,
    autoSendHoliday: Boolean
  )

  @derive(decoder, encoder, show)
  case class UpdateTemplateOfBirthday(
    smsMenId: Option[TemplateId] = None,
    smsWomenId: Option[TemplateId] = None
  )
}
