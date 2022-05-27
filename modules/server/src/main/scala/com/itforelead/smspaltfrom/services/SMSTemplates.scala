package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.{ID, SMSTemplate}
import com.itforelead.smspaltfrom.domain.SMSTemplate.CreateSMSTemplate
import com.itforelead.smspaltfrom.domain.types.TemplateId
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.{Session, Void}

trait SMSTemplates[F[_]] {
  def create(form: CreateSMSTemplate): F[SMSTemplate]
  def templates: F[List[SMSTemplate]]
}

object SMSTemplates {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): SMSTemplates[F] =
    new SMSTemplates[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql._

      def create(form: CreateSMSTemplate): F[SMSTemplate] = {
        for {
          id <- ID.make[F, TemplateId]
          sms <- prepQueryUnique(
            insert,
            SMSTemplate(id, form.text, form.active)
          )
        } yield sms
      }

      override def templates: F[List[SMSTemplate]] =
        prepQueryList(select, Void)
    }
}
