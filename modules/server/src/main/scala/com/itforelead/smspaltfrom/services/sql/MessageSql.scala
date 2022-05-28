package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Message
import com.itforelead.smspaltfrom.domain.types.MessageId
import com.itforelead.smspaltfrom.services.sql.ContactsSql.contactId
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import skunk._
import skunk.codec.all.timestamp
import skunk.implicits._

object MessageSql {
  val messageId: Codec[MessageId] = identity[MessageId]

  private val Columns = messageId ~ timestamp ~ contactId ~ templateId ~ timestamp ~ deliveryStatus

  val encoder: Encoder[Message] =
    Columns.contramap(m => m.id ~ m.createdAt ~ m.contactId ~ m.templateId ~ m.sentDate ~ m.deliveryStatus)

  val decoder: Decoder[Message] =
    Columns.map { case id ~ createdAt ~ contactId ~ templateId ~ sentDate ~ deliveryStatus =>
      Message(id, createdAt, contactId, templateId, sentDate, deliveryStatus)
    }

  val insert: Query[Message, Message] =
    sql"""INSERT INTO messages VALUES ($encoder) returning *""".query(decoder)

}