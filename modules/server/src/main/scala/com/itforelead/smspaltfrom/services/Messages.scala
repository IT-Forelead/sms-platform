package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Message.CreateMessage
import com.itforelead.smspaltfrom.domain.{ID, Message}
import com.itforelead.smspaltfrom.domain.types.MessageId
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.Session

import java.time.LocalDateTime

trait Messages[F[_]] {
  def create(form: CreateMessage): F[Message]
}

object Messages {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): Messages[F] =
    new Messages[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.MessageSql._

      def create(form: CreateMessage): F[Message] = {
        for {
          id  <- ID.make[F, MessageId]
          now <- Sync[F].delay(LocalDateTime.now())
          message <- prepQueryUnique(
            insert,
            Message(id, now, form.contactId, form.templateId, form.sentDate, form.deliveryStatus)
          )
        } yield message
      }

    }
}
