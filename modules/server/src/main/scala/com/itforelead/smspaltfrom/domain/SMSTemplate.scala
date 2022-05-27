package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.types.{Content, TemplateId}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder, show)
case class SMSTemplate(
  id: TemplateId,
  text: Content,
  active: Boolean = false
)

object SMSTemplate {
  @derive(decoder, encoder, show)
  case class CreateSMSTemplate(text: Content, active: Boolean)
}
