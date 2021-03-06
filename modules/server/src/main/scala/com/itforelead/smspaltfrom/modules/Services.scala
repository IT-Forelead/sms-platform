package com.itforelead.smspaltfrom.modules

import cats.effect.{Async, Resource}
import com.itforelead.smspaltfrom.config.{BrokerConfig, SchedulerConfig}
import com.itforelead.smspaltfrom.effects.{Background, GenUUID}
import com.itforelead.smspaltfrom.services._
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import skunk.Session

object Services {
  def apply[F[_]: Async: GenUUID: Logger: Background](
    brokerConfig: BrokerConfig,
    schedulerConfig: SchedulerConfig,
    httpClient: Client[F]
  )(implicit session: Resource[F, Session[F]]): Services[F] = {
    val users         = Users[F]
    val contacts      = Contacts[F]
    val settings      = SystemSettings[F]
    val smsTemplates  = SMSTemplates[F]
    val messages      = Messages[F]
    val holidays      = Holidays[F]
    val messageBroker = MessageBroker[F](httpClient, brokerConfig)

    new Services[F](
      users = users,
      contacts = contacts,
      holidays = holidays,
      messages = messages,
      smsTemplates = smsTemplates,
      systemSettings = settings,
      templateCategories = TemplateCategories[F],
      congratulator = Congratulator
        .make[F](users, contacts, holidays, smsTemplates, messages, settings, messageBroker, schedulerConfig)
    )
  }
}

final class Services[F[_]] private (
  val users: Users[F],
  val contacts: Contacts[F],
  val messages: Messages[F],
  val holidays: Holidays[F],
  val smsTemplates: SMSTemplates[F],
  val systemSettings: SystemSettings[F],
  val templateCategories: TemplateCategories[F],
  val congratulator: Congratulator[F]
)
