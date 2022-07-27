package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.SMSTemplate.SMSTemplateWithCatName
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, TemplateId, UserId}
import com.itforelead.smspaltfrom.services.sql.UserSQL.userId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object SMSTemplateSql {
  val templateId: Codec[TemplateId]                 = identity[TemplateId]
  val templateCategoryId: Codec[TemplateCategoryId] = identity[TemplateCategoryId]

  private val Columns = templateId ~ userId ~ templateCategoryId ~ title ~ content ~ gender ~ bool

  private val ColumnsWithCatName =
    templateId ~ userId ~ templateCategoryId ~ title ~ content ~ gender ~ bool ~ templateCategoryName

  val encoder: Encoder[SMSTemplate] =
    Columns.contramap(sms => sms.id ~ sms.userId ~ sms.templateCategoryId ~ sms.title ~ sms.text ~ sms.gender ~ false)

  val decoder: Decoder[SMSTemplate] =
    Columns.map { case id ~ userId ~ templateCategoryId ~ title ~ text ~ gender ~ _ =>
      SMSTemplate(id, userId, templateCategoryId, title, text, gender)
    }

  val decSMSTemplateWithCatName: Decoder[SMSTemplateWithCatName] =
    ColumnsWithCatName.map {
      case id ~ userId ~ templateCategoryId ~ title ~ text ~ genderAccess ~ _ ~ templateCategoryName =>
        SMSTemplateWithCatName(id, userId, templateCategoryId, title, text, genderAccess, templateCategoryName)
    }

  val insert: Query[SMSTemplate, SMSTemplate] =
    sql"""INSERT INTO sms_templates VALUES ($encoder) returning *""".query(decoder)

  val select: Query[UserId, SMSTemplateWithCatName] =
    sql"""SELECT t.*, tc.name FROM sms_templates t
         INNER JOIN template_categories tc ON tc.id = t.template_category_id AND tc.deleted = false
         WHERE t.user_id = $userId AND t.deleted = false""".query(decSMSTemplateWithCatName)

  val findById: Query[TemplateId, SMSTemplate] =
    sql"""SELECT * FROM sms_templates WHERE id = $templateId AND deleted = false""".query(decoder)

  val updateSql: Query[SMSTemplate, SMSTemplate] =
    sql"""UPDATE sms_templates
         SET template_category_id = $templateCategoryId,
             title = $title,
             text = $content,
             gender_access = $gender
         WHERE id = $templateId AND $userId RETURNING *"""
      .query(decoder)
      .contramap[SMSTemplate](tmpl =>
        tmpl.templateCategoryId ~ tmpl.title ~ tmpl.text ~ tmpl.gender ~ tmpl.id ~ tmpl.userId
      )

  val deleteSql: Command[TemplateId ~ UserId] =
    sql"""UPDATE sms_templates SET deleted = true WHERE id = $templateId AND user_id = $userId""".command

}
