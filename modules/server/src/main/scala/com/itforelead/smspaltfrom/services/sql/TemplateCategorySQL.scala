package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.TemplateCategory
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, UserId}
import com.itforelead.smspaltfrom.services.sql.UserSQL.userId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object TemplateCategorySQL {
  val templateCategoryId: Codec[TemplateCategoryId] = identity[TemplateCategoryId]

  private val Columns = templateCategoryId ~ userId ~ templateCategoryName ~ bool

  val encoder: Encoder[TemplateCategory] =
    Columns.contramap(tc => tc.id ~ tc.userId ~ tc.name ~ false)

  val decoder: Decoder[TemplateCategory] =
    Columns.map { case id ~ userId ~ name ~ _ => TemplateCategory(id, userId, name) }

  val insert: Query[TemplateCategory, TemplateCategory] =
    sql"""INSERT INTO template_categories VALUES ($encoder) returning *""".query(decoder)

  val updateSql: Query[TemplateCategory, TemplateCategory] =
    sql"""UPDATE template_categories
         SET name = $templateCategoryName WHERE id = $templateCategoryId AND user_id = $userId RETURNING *"""
      .query(decoder)
      .contramap[TemplateCategory](tmpl => tmpl.name ~ tmpl.id ~ tmpl.userId)

  val select: Query[UserId, TemplateCategory] =
    sql"""SELECT * FROM template_categories WHERE user_id = $userId AND deleted = false""".query(decoder)

  val deleteSql: Command[TemplateCategoryId ~ UserId] =
    sql"""UPDATE template_categories SET deleted = true WHERE id = $templateCategoryId AND user_id = $userId""".command

}
