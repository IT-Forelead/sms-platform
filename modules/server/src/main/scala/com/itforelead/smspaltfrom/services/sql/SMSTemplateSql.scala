package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.types.TemplateId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object SMSTemplateSql {
  val templateId: Codec[TemplateId] = identity[TemplateId]

  private val Columns = templateId ~ content ~ bool ~ bool

  val encoder: Encoder[SMSTemplate] =
    Columns.contramap(sms => sms.id ~ sms.text ~ sms.active ~ false)

  val decoder: Decoder[SMSTemplate] =
    Columns.map { case id ~ text ~ active ~ _ =>
      SMSTemplate(id, text, active)
    }

  val insert: Query[SMSTemplate, SMSTemplate] =
    sql"""INSERT INTO sms_templates VALUES ($encoder) returning *""".query(decoder)

  val select: Query[Void, SMSTemplate] =
    sql"""SELECT * FROM sms_templates WHERE deleted = false""".query(decoder)

  val updateSql: Query[SMSTemplate, SMSTemplate] =
    sql"""UPDATE sms_templates
         SET text = $content, active = $bool
         WHERE id = $templateId RETURNING *"""
      .query(decoder)
      .contramap[SMSTemplate](tmpl => tmpl.text ~ tmpl.active ~ tmpl.id)

  val deleteSql: Command[TemplateId] =
    sql"""UPDATE sms_templates SET deleted = true WHERE id = $templateId""".command

}
