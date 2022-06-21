package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Message.MessageWithContact
import com.itforelead.smspaltfrom.domain.{DeliveryStatus, Message}
import com.itforelead.smspaltfrom.domain.types.{ContactId, MessageId}
import com.itforelead.smspaltfrom.services.sql.ContactsSql.contactId
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import skunk._
import skunk.codec.all.timestamp
import skunk.implicits._

object MessageSql {
  val messageId: Codec[MessageId] = identity[MessageId]

  private val Columns = messageId ~ contactId ~ templateId ~ timestamp ~ deliveryStatus

  val encoder: Encoder[Message] =
    Columns.contramap(m => m.id ~ m.contactId ~ m.templateId ~ m.sentDate ~ m.deliveryStatus)

  val decoder: Decoder[Message] =
    Columns.map { case id ~ contactId ~ templateId ~ sentDate ~ deliveryStatus =>
      Message(id, contactId, templateId, sentDate, deliveryStatus)
    }

  private val MessageColumns = decoder ~ ContactsSql.decoder ~ SMSTemplateSql.decoder

  val decMessageWithContact: Decoder[MessageWithContact] =
    MessageColumns.map { case message ~ contact ~ template =>
      MessageWithContact(message, contact, template)
    }

  val insert: Query[Message, Message] =
    sql"""INSERT INTO messages VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[Void, MessageWithContact] =
    sql"""SELECT messages.*, contacts.*, sms_templates.* FROM messages
          INNER JOIN contacts ON contacts.id = messages.contact_id
          INNER JOIN sms_templates ON sms_templates.id = messages.sms_temp_id
       """.query(decMessageWithContact)

  val selectByContactId: Query[ContactId, MessageWithContact] =
    sql"""SELECT * FROM messages WHERE contact_id = $contactId""".query(decMessageWithContact)

  val changeStatusSql: Query[DeliveryStatus ~ MessageId, Message] =
    sql"""UPDATE messages SET delivery_status = $deliveryStatus WHERE id = $messageId RETURNING *""".query(decoder)

}
