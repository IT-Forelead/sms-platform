package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.SMSTemplate.{CreateSMSTemplate, SMSTemplateWithCatName, UpdateSMSTemplate}
import com.itforelead.smspaltfrom.domain.types.{TemplateId, UserId}
import com.itforelead.smspaltfrom.domain.{ID, SMSTemplate}
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.Session
import skunk.implicits.toIdOps

trait SMSTemplates[F[_]] {
  def create(userId: UserId, form: CreateSMSTemplate): F[SMSTemplate]
  def find(templateId: TemplateId): F[Option[SMSTemplate]]
  def templates(userId: UserId): F[List[SMSTemplateWithCatName]]
  def update(userId: UserId, tmpl: UpdateSMSTemplate): F[SMSTemplate]
  def delete(id: TemplateId, userId: UserId): F[Unit]
}

object SMSTemplates {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): SMSTemplates[F] =
    new SMSTemplates[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql._

      override def create(userId: UserId, form: CreateSMSTemplate): F[SMSTemplate] =
        ID.make[F, TemplateId].flatMap { id =>
          prepQueryUnique(
            insert,
            SMSTemplate(id, userId, form.templateCategoryId, form.title, form.text, form.gender)
          )
        }

      override def find(templateId: TemplateId): F[Option[SMSTemplate]] =
        prepOptQuery(findById, templateId)

      override def templates(userId: UserId): F[List[SMSTemplateWithCatName]] =
        prepQueryList(select, userId)

      override def update(userId: UserId, tmpl: UpdateSMSTemplate): F[SMSTemplate] =
        prepQueryUnique(
          updateSql,
          SMSTemplate(tmpl.id, userId, tmpl.templateCategoryId, tmpl.title, tmpl.text, tmpl.gender)
        )

      override def delete(id: TemplateId, userId: UserId): F[Unit] =
        prepCmd(deleteSql, id ~ userId)
    }
}
