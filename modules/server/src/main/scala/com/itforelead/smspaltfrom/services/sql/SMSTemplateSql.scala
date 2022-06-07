package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, TemplateId}
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object SMSTemplateSql {
  val templateId: Codec[TemplateId]                 = identity[TemplateId]
  val templateCategoryId: Codec[TemplateCategoryId] = identity[TemplateCategoryId]

  private val Columns = templateId ~ templateCategoryId ~ title ~ content ~ genderAccess ~ bool ~ bool

  val encoder: Encoder[SMSTemplate] =
    Columns.contramap(sms =>
      sms.id ~ sms.templateCategoryId ~ sms.title ~ sms.text ~ sms.genderAccess ~ sms.active ~ false
    )

  val decoder: Decoder[SMSTemplate] =
    Columns.map { case id ~ templateCategoryId ~ title ~ text ~ genderAccess ~ active ~ _ =>
      SMSTemplate(id, templateCategoryId, title, text, genderAccess, active)
    }

  val insert: Query[SMSTemplate, SMSTemplate] =
    sql"""INSERT INTO sms_templates VALUES ($encoder) returning *""".query(decoder)

  val select: Query[Void, SMSTemplate] =
    sql"""SELECT * FROM sms_templates WHERE deleted = false""".query(decoder)

  val updateSql: Query[SMSTemplate, SMSTemplate] =
    sql"""UPDATE sms_templates
         SET template_category_id = $templateCategoryId,
             title = $title,
             text = $content,
             gender_access = $genderAccess,
             active = $bool
         WHERE id = $templateId RETURNING *"""
      .query(decoder)
      .contramap[SMSTemplate](tmpl =>
        tmpl.templateCategoryId ~ tmpl.title ~ tmpl.text ~ tmpl.genderAccess ~ tmpl.active ~ tmpl.id
      )

  val deleteSql: Command[TemplateId] =
    sql"""UPDATE sms_templates SET deleted = true WHERE id = $templateId""".command

}
