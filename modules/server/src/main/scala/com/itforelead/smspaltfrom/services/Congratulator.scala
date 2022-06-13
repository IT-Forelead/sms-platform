package com.itforelead.smspaltfrom.services

import cats.data.OptionT
import cats.implicits.{toFlatMapOps, toFunctorOps, toTraverseOps}
import cats.{Applicative, Monad}
import com.itforelead.smspaltfrom.domain.circe._
import com.itforelead.smspaltfrom.domain.custom.RedisStaticKeys.TemplateIdCache
import com.itforelead.smspaltfrom.domain.types.TemplateId
import com.itforelead.smspaltfrom.domain.{Contact, Gender, SMSTemplate}
import com.itforelead.smspaltfrom.implicits.CirceDecoderOps
import com.itforelead.smspaltfrom.services.redis.RedisClient
import eu.timepit.refined.auto._
import org.typelevel.log4cats.Logger

import java.time.LocalDate

trait Congratulator[F[_]] {
  def start: F[Unit]
}

object Congratulator {
  def make[F[_]: Applicative: Monad: Logger](
    contacts: Contacts[F],
    smsTemplates: SMSTemplates[F],
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
                .map(prepare(_, contact))
                .cataF(
                  Logger[F].debug(s"Has not selected template id for gender [ ${contact.gender} ]"),
                  send(contact)
                )
            }
          }
          .void

      private def prepare(template: SMSTemplate, contact: Contact): String =
        template.text.value
          .replace("[firstName]", contact.firstName.value)
          .replace("[lastName]", contact.firstName.value)

      private def send(contact: Contact)(text: String): F[Unit] =
        Logger[F].info(
          s"""Congratulation message sent to [ ${contact.phone} ],
              message text [
                $text
              ] """
        )

    }

}
