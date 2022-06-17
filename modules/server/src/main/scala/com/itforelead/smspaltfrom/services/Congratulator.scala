package com.itforelead.smspaltfrom.services

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.{catsSyntaxFlatMapOps, toFlatMapOps, toFunctorOps, toTraverseOps}
import com.itforelead.smspaltfrom.domain.Message.CreateMessage
import com.itforelead.smspaltfrom.domain.circe._
import com.itforelead.smspaltfrom.domain.custom.RedisStaticKeys.TemplateIdCache
import com.itforelead.smspaltfrom.domain.types.{ContactId, TemplateId}
import com.itforelead.smspaltfrom.domain.{Contact, DeliveryStatus, Gender, Message, SMSTemplate}
import com.itforelead.smspaltfrom.implicits.CirceDecoderOps
import com.itforelead.smspaltfrom.services.redis.RedisClient
import eu.timepit.refined.auto._
import org.typelevel.log4cats.Logger

import java.time.{LocalDate, LocalDateTime}

trait Congratulator[F[_]] {
  def start: F[Unit]
}

object Congratulator {
  def make[F[_]: Sync: Logger](
    contacts: Contacts[F],
    smsTemplates: SMSTemplates[F],
    messages: Messages[F],
    redis: RedisClient[F]
  ): Congratulator[F] =
    new Congratulator[F] {
      override def start: F[Unit] =
        OptionT(redis.get(TemplateIdCache))
          .map(_.as[Map[Gender, TemplateId]])
          .cataF(
            Logger[F].debug("Has not selected template id"),
            findAndSend
          )

      private def findAndSend(ids: Map[Gender, TemplateId]): F[Unit] =
        contacts
          .findByBirthday(LocalDate.now())
          .flatMap { contacts =>
            contacts.traverse { contact =>
              OptionT(ids.get(contact.gender).flatTraverse(smsTemplates.find))
                .map(template => template.id -> prepare(template, contact))
                .cataF(
                  Logger[F].debug(s"Has not selected template id for gender [ ${contact.gender} ]"),
                  { case (templateId, text) =>
                    createMessage(contact.id, templateId).flatMap { message =>
                      send(contact, text, message)
                    }
                  }
                )
            }
          }
          .void

      private def createMessage(contactId: ContactId, templateId: TemplateId): F[Message] =
        Sync[F].delay(LocalDateTime.now()).flatMap { now =>
          messages.create(CreateMessage(contactId, templateId, now, DeliveryStatus.SENT))
        }

      private def prepare(template: SMSTemplate, contact: Contact): String =
        template.text.value
          .replace("[firstName]", contact.firstName.value)
          .replace("[lastName]", contact.firstName.value)

      private def send(contact: Contact, text: String, message: Message): F[Unit] =
        messages.changeStatus(message.id, status = DeliveryStatus.DELIVERED) >>
          Logger[F].info(
            s"""Congratulation message sent to [ ${contact.phone} ],
              message text [
                $text
              ] """
          )

    }

}
