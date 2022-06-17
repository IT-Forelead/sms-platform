package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.types.TemplateId
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder, show)
case class SystemSetting(
  smsWomenId: Option[TemplateId],
  smsMenId: Option[TemplateId],
  autoSendBirthday: Boolean,
  autoSendHoliday: Boolean,
  darkTheme: Boolean
)
