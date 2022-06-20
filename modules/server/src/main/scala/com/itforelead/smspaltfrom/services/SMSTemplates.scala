package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.SMSTemplate.{CreateSMSTemplate, SMSTemplateWithCatName}
import com.itforelead.smspaltfrom.domain.types.TemplateId
import com.itforelead.smspaltfrom.domain.{ID, SMSTemplate}
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.Session

trait SMSTemplates[F[_]] {
  def create(form: CreateSMSTemplate): F[SMSTemplate]
  def find(templateId: TemplateId): F[Option[SMSTemplate]]
  def templates: F[List[SMSTemplateWithCatName]]
  def update(tmpl: SMSTemplate): F[SMSTemplate]
  def delete(id: TemplateId): F[Unit]
}

object SMSTemplates {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): SMSTemplates[F] =
    new SMSTemplates[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql._

      override def create(form: CreateSMSTemplate): F[SMSTemplate] =
        ID.make[F, TemplateId].flatMap { id =>
          prepQueryUnique(
            insert,
            SMSTemplate(id, form.templateCategoryId, form.title, form.text, form.gender)
          )
        }

      override def find(templateId: TemplateId): F[Option[SMSTemplate]] =
        prepOptQuery(findById, templateId)

      override def templates: F[List[SMSTemplateWithCatName]] =
        prepQueryAll(select)

      override def update(tmpl: SMSTemplate): F[SMSTemplate] =
        prepQueryUnique(updateSql, tmpl)

      override def delete(id: TemplateId): F[Unit] =
        prepCmd(deleteSql, id)
    }
}
