package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Message.{CreateMessage, MessageWithContact}
import com.itforelead.smspaltfrom.domain.types.{ContactId, MessageId}
import com.itforelead.smspaltfrom.domain.{DeliveryStatus, ID, Message, MessageReport}
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk.implicits.toIdOps
import skunk.{Session, Void}

trait Messages[F[_]] {
  def create(msg: CreateMessage): F[Message]
  def messages: F[List[MessageWithContact]]
  def messagesByContactId(id: ContactId): F[List[MessageWithContact]]
  def changeStatus(id: MessageId, status: DeliveryStatus): F[Message]
  def getReport: F[List[MessageReport]]
}

object Messages {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): Messages[F] =
    new Messages[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.MessageSql._

      override def create(msg: CreateMessage): F[Message] =
        ID.make[F, MessageId].flatMap { id =>
          prepQueryUnique(
            insert,
            Message(id, msg.contactId, msg.templateId, msg.sentDate, msg.deliveryStatus)
          )
        }

      override def messages: F[List[MessageWithContact]] =
        prepQueryList(select, Void)

      override def messagesByContactId(id: ContactId): F[List[MessageWithContact]] =
        prepQueryList(selectByContactId, id)

      override def changeStatus(id: MessageId, status: DeliveryStatus): F[Message] =
        prepQueryUnique(changeStatusSql, status ~ id)

      override def getReport: F[List[MessageReport]] = {
        val af = selectReport(DeliveryStatus.DELIVERED)
        prepQueryList(af.fragment.query(reportDecoder), af.argument)
      }
    }
}
