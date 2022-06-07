package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.{SMSTemplate, TemplateCategory}
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, TemplateId}
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.{decoder, templateCategoryId, templateId}
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object TemplateCategorySQL {
  val templateCategoryId: Codec[TemplateCategoryId] = identity[TemplateCategoryId]

  private val Columns = templateCategoryId ~ templateCategoryName ~ bool

  val encoder: Encoder[TemplateCategory] =
    Columns.contramap(sms => sms.id ~ sms.name ~ false)

  val decoder: Decoder[TemplateCategory] =
    Columns.map { case id ~ name ~ _ => TemplateCategory(id, name) }

  val insert: Query[TemplateCategory, TemplateCategory] =
    sql"""INSERT INTO template_categories VALUES ($encoder) returning *""".query(decoder)

  val updateSql: Query[TemplateCategory, TemplateCategory] =
    sql"""UPDATE template_categories
         SET name = $templateCategoryName WHERE id = $templateCategoryId RETURNING *"""
      .query(decoder)
      .contramap[TemplateCategory](tmpl => tmpl.name ~ tmpl.id)

  val select: Query[Void, TemplateCategory] =
    sql"""SELECT * FROM template_categories WHERE deleted = false""".query(decoder)

  val deleteSql: Command[TemplateCategoryId] =
    sql"""UPDATE template_categories SET deleted = true WHERE id = $templateCategoryId""".command

}
