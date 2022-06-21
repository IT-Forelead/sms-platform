package com.itforelead.smspaltfrom.services

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxFlatMapOps, toFlatMapOps, toFunctorOps, toTraverseOps}
import com.itforelead.smspaltfrom.domain.Gender.{ALL, FEMALE, MALE}
import com.itforelead.smspaltfrom.domain.Message.CreateMessage
import com.itforelead.smspaltfrom.domain.custom.exception.GenderIncorrect
import com.itforelead.smspaltfrom.domain.types.{ContactId, TemplateId}
import com.itforelead.smspaltfrom.domain.{Contact, DeliveryStatus, Gender, Message, SMSTemplate, SystemSetting}
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
    settings: SystemSettings[F],
    messageBroker: MessageBroker[F]
  ): Congratulator[F] =
    new Congratulator[F] {
      override def start: F[Unit] =
        OptionT(settings.settings)
          .cataF(
            Logger[F].debug("Has not selected template id"),
            findAndSend
          )

      def retrieveTemplateId(setting: SystemSetting): Gender => Option[TemplateId] = {
        case MALE   => setting.smsMenId
        case FEMALE => setting.smsWomenId
        case ALL    => throw GenderIncorrect(ALL)
      }

      private def findAndSend(setting: SystemSetting): F[Unit] =
        contacts
          .findByBirthday(LocalDate.now())
          .flatMap { contacts =>
            contacts.traverse { contact =>
              OptionT(retrieveTemplateId(setting)(contact.gender).flatTraverse(smsTemplates.find))
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
          .replace("[FIRSTNAME]", contact.firstName.value)
          .replace("[LASTNAME]", contact.lastName.value)

      private def send(contact: Contact, text: String, message: Message): F[Unit] =
        messages.changeStatus(message.id, status = DeliveryStatus.DELIVERED) >>
          messageBroker.send(message.id, contact.phone, text)

    }

}
