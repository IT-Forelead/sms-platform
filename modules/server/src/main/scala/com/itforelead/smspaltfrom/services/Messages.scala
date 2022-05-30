package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Message.CreateMessage
import com.itforelead.smspaltfrom.domain.{DeliveryStatus, ID, Message}
import com.itforelead.smspaltfrom.domain.types.MessageId
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.Session
import skunk.implicits.toIdOps

import java.time.LocalDateTime

trait Messages[F[_]] {
  def create(msg: CreateMessage): F[Message]
  def changeStatus(id: MessageId, status: DeliveryStatus): F[Message]
}

object Messages {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): Messages[F] =
    new Messages[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.MessageSql._

      override def create(msg: CreateMessage): F[Message] = {
        for {
          id  <- ID.make[F, MessageId]
          now <- Sync[F].delay(LocalDateTime.now())
          message <- prepQueryUnique(
            insert,
            Message(id, now, msg.contactId, msg.templateId, msg.sentDate, msg.deliveryStatus)
          )
        } yield message
      }

      override def changeStatus(id: MessageId, status: DeliveryStatus): F[Message] =
        prepQueryUnique(changeStatusSql, status ~ id)

    }
}
