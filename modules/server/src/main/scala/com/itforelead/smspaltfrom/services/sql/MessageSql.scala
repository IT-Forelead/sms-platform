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

  private val Columns = messageId ~ timestamp ~ contactId ~ templateId ~ timestamp ~ deliveryStatus

  private val MessageColumns = MessageSql.decoder ~ ContactsSql.decoder ~ SMSTemplateSql.decoder

  val encoder: Encoder[Message] =
    Columns.contramap(m => m.id ~ m.createdAt ~ m.contactId ~ m.templateId ~ m.sentDate ~ m.deliveryStatus)

  val decoder: Decoder[Message] =
    Columns.map { case id ~ createdAt ~ contactId ~ templateId ~ sentDate ~ deliveryStatus =>
      Message(id, createdAt, contactId, templateId, sentDate, deliveryStatus)
    }

  val decMessageWithContact: Decoder[MessageWithContact] =
    MessageColumns.map { case message ~ contact ~ template =>
      MessageWithContact(message, contact, template)
    }

  val insert: Query[Message, Message] =
    sql"""INSERT INTO messages VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[Void, MessageWithContact] =
    sql"""SELECT * FROM messages""".query(decMessageWithContact)

  val selectByContactId: Query[ContactId, MessageWithContact] =
    sql"""SELECT * FROM messages WHERE contact_id = $contactId""".query(decMessageWithContact)

  val changeStatusSql: Query[DeliveryStatus ~ MessageId, Message] =
    sql"""UPDATE messages SET delivery_status = $deliveryStatus WHERE id = $messageId RETURNING *""".query(decoder)

}
