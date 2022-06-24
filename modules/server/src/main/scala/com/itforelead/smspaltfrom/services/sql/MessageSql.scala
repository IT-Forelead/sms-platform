package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Message.MessageWithContact
import com.itforelead.smspaltfrom.domain.types.MessageId
import com.itforelead.smspaltfrom.domain.{DeliveryStatus, Message, MessageReport}
import com.itforelead.smspaltfrom.services.sql.ContactsSql.contactId
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import skunk._
import skunk.codec.all.{int4, timestamp}
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

  val reportDecoder: Decoder[MessageReport] =
    (SMSTemplateSql.decoder ~ templateCategoryName ~ int4 ~ int4 ~ timestamp).map {
      case template ~ category ~ total ~ delivered ~ sentDate =>
        MessageReport(template, category, total, delivered, sentDate)
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

  val changeStatusSql: Query[DeliveryStatus ~ MessageId, Message] =
    sql"""UPDATE messages SET delivery_status = $deliveryStatus WHERE id = $messageId RETURNING *""".query(decoder)

  def selectReport(status: DeliveryStatus): AppliedFragment =
    sql"""SELECT t.*, tc.name, COUNT(m.*),
           COUNT(m.*) FILTER (WHERE m.delivery_status = $deliveryStatus) as delivered,
           MIN(m.sent_date) AS sent_date 
           FROM sms_templates t
           INNER JOIN messages m ON t.id = m.sms_temp_id
           INNER JOIN template_categories tc ON tc.id = t.template_category_id AND tc.deleted = false
           WHERE t.deleted = false GROUP BY t.id, tc.name
           """.apply(status)
}
