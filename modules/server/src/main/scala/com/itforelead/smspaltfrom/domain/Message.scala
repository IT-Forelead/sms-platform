package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.types.{ContactId, MessageId, TemplateId}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

import java.time.LocalDateTime

@derive(decoder, encoder, show)
case class Message(
  id: MessageId,
  contactId: ContactId,
  templateId: TemplateId,
  sentDate: LocalDateTime,
  deliveryStatus: DeliveryStatus
)

object Message {
  @derive(decoder, encoder, show)
  case class CreateMessage(
    contactId: ContactId,
    templateId: TemplateId,
    sentDate: LocalDateTime,
    deliveryStatus: DeliveryStatus
  )

  @derive(decoder, encoder, show)
  case class MessageWithContact(
    message: Message,
    contact: Contact,
    template: SMSTemplate
  )
}
