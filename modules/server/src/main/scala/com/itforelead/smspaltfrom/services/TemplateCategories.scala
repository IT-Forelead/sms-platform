package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.domain.TemplateCategory.{CreateTemplateCategory, UpdateTemplateCategory}
import com.itforelead.smspaltfrom.domain.{ID, TemplateCategory}
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, UserId}
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.{Session, Void}
import skunk.implicits.toIdOps
import cats.implicits._

trait TemplateCategories[F[_]] {
  def create(userId: UserId, form: CreateTemplateCategory): F[TemplateCategory]
  def templateCategories(userId: UserId): F[List[TemplateCategory]]
  def update(userId: UserId, tmpl: UpdateTemplateCategory): F[TemplateCategory]
  def delete(id: TemplateCategoryId, userId: UserId): F[Unit]
}

object TemplateCategories {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): TemplateCategories[F] =
    new TemplateCategories[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.TemplateCategorySQL._

      override def create(userId: UserId, form: CreateTemplateCategory): F[TemplateCategory] = {
        ID.make[F, TemplateCategoryId].flatMap { id =>
          prepQueryUnique(
            insert,
            TemplateCategory(id, userId, form.name)
          )
        }
      }

      override def templateCategories(userId: UserId): F[List[TemplateCategory]] =
        prepQueryList(select, userId)

      override def update(userId: UserId, tmpl: UpdateTemplateCategory): F[TemplateCategory] =
        prepQueryUnique(updateSql, TemplateCategory(tmpl.id, userId, tmpl.name))

      override def delete(id: TemplateCategoryId, userId: UserId): F[Unit] = {
        prepCmd(deleteSql, id ~ userId)
      }
    }
}
