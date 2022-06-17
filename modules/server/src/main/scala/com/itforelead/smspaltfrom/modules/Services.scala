package com.itforelead.smspaltfrom.modules

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.services._
import com.itforelead.smspaltfrom.services.redis.RedisClient
import org.typelevel.log4cats.Logger
import skunk.Session

object Services {
  def apply[F[_]: Sync: GenUUID: Logger](
    redis: RedisClient[F]
  )(implicit session: Resource[F, Session[F]]): Services[F] = {
    val contacts     = Contacts[F]
    val settings     = SystemSettings[F]
    val smsTemplates = SMSTemplates[F](redis)
    new Services[F](
      users = Users[F],
      contacts = contacts,
      messages = Messages[F],
      holidays = Holidays[F],
      smsTemplates = smsTemplates,
      systemSettings = settings,
      templateCategories = TemplateCategories[F],
      congratulator = Congratulator.make[F](contacts, smsTemplates, redis)
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
