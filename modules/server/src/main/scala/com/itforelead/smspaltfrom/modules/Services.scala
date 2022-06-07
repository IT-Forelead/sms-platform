package com.itforelead.smspaltfrom.modules

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.services.{
  Congratulator,
  Contacts,
  Holidays,
  Messages,
  SMSTemplates,
  TemplateCategories,
  Users
}
import skunk.Session

object Services {
  def apply[F[_]: Sync: GenUUID](implicit session: Resource[F, Session[F]]): Services[F] = {
    val contacts = Contacts[F]
    new Services[F](
      users = Users[F],
      contacts = contacts,
      messages = Messages[F],
      holidays = Holidays[F],
      smsTemplates = SMSTemplates[F],
      templateCategories = TemplateCategories[F],
      congratulator = Congratulator.make[F](contacts)
    )
  }
}

final class Services[F[_]] private (
  val users: Users[F],
  val contacts: Contacts[F],
  val messages: Messages[F],
  val holidays: Holidays[F],
  val smsTemplates: SMSTemplates[F],
  val templateCategories: TemplateCategories[F],
  val congratulator: Congratulator[F]
)
