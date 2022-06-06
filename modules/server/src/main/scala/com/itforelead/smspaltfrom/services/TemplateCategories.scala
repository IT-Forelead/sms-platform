package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.domain.TemplateCategory.CreateTemplateCategory
import com.itforelead.smspaltfrom.domain.{ID, TemplateCategory}
import com.itforelead.smspaltfrom.domain.types.TemplateCategoryId
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.{Session, Void}
import cats.implicits._

trait TemplateCategories[F[_]] {
  def create(form: CreateTemplateCategory): F[TemplateCategory]
  def templates: F[List[TemplateCategory]]
  def update(tmpl: TemplateCategory): F[TemplateCategory]
  def delete(id: TemplateCategoryId): F[Unit]
}

object TemplateCategories {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): TemplateCategories[F] =
    new TemplateCategories[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.TemplateCategorySQL._

      override def create(form: CreateTemplateCategory): F[TemplateCategory] = {
        ID.make[F, TemplateCategoryId].flatMap { id =>
          prepQueryUnique(
            insert,
            TemplateCategory(id, form.name)
          )
        }
      }

      override def templates: F[List[TemplateCategory]] =
        prepQueryList(select, Void)

      override def update(tmpl: TemplateCategory): F[TemplateCategory] =
        prepQueryUnique(updateSql, tmpl)

      override def delete(id: TemplateCategoryId): F[Unit] =
        prepCmd(deleteSql, id)
    }
}
