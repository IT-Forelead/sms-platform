package com.itforelead.smspaltfrom.services

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import com.itforelead.smspaltfrom.config.SchedulerConfig
import com.itforelead.smspaltfrom.domain.Gender._
import com.itforelead.smspaltfrom.domain.Message.CreateMessage
import com.itforelead.smspaltfrom.domain._
import com.itforelead.smspaltfrom.domain.custom.exception.GenderIncorrect
import com.itforelead.smspaltfrom.domain.types.{ContactId, TemplateId}
import com.itforelead.smspaltfrom.effects.Background
import eu.timepit.refined.auto._
import org.typelevel.log4cats.Logger

import java.time.{LocalDate, LocalDateTime, LocalTime}
import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait Congratulator[F[_]] {
  def start: F[Unit]
}

object Congratulator {
  def make[F[_]: Sync: Logger: Background](
    contacts: Contacts[F],
    holidays: Holidays[F],
    smsTemplates: SMSTemplates[F],
    messages: Messages[F],
    settings: SystemSettings[F],
    messageBroker: MessageBroker[F],
    schedulerConfig: SchedulerConfig
  ): Congratulator[F] =
    new Congratulator[F] {

      private val fixedTime: FiniteDuration = {
        val now = schedulerConfig.startTime.toSecondOfDay - LocalTime.now.toSecondOfDay
        if (now <= 0) 1.minute else now.seconds
      }

      override def start: F[Unit] =
        Logger[F].debug(Console.GREEN + s"Conguratulator will start after $fixedTime" + Console.RESET) >>
          Background[F].schedule(init, fixedTime, schedulerConfig.period)

      def init: F[Unit] =
        for {
          _ <- startSendHolidays
          _ <- OptionT(settings.settings)
            .cataF(
              Logger[F].debug("Setting not found!"),
              findAndSend
            )
        } yield ()

      private def startSendHolidays: F[Unit] =
        for {
          holidaysList <- holidays.holidaysOfToday
          contactsList <- contacts.contacts
          _ <- holidaysList.flatTraverse { holiday =>
            contactsList.traverse { contact =>
              prepareTextAndSend(contact)(OptionT(holiday.smsMenId.flatTraverse(smsTemplates.find))) >>
                prepareTextAndSend(contact)(OptionT(holiday.smsWomenId.flatTraverse(smsTemplates.find)))
            }
          }
        } yield ()

      private def prepareTextAndSend(contact: Contact): OptionT[F, SMSTemplate] => F[Unit] =
        _.map(template => template.id -> prepare(template, contact))
          .cataF(
            Logger[F].debug(s"Has not selected template id for gender [ ${contact.gender} ]"),
            { case (templateId, text) =>
              createMessage(contact.id, templateId).flatMap { message =>
                send(contact, text, message)
              }
            }
          )

      private def retrieveTemplateId(setting: SystemSetting): Gender => Option[TemplateId] = {
        case MALE   => setting.smsMenId
        case FEMALE => setting.smsWomenId
        case ALL    => throw GenderIncorrect(ALL)
      }

      private def findAndSend(setting: SystemSetting): F[Unit] =
        contacts
          .findByBirthday(LocalDate.now())
          .flatMap { contacts =>
            contacts.traverse { contact =>
              prepareTextAndSend(contact)(
                OptionT(retrieveTemplateId(setting)(contact.gender).flatTraverse(smsTemplates.find))
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
