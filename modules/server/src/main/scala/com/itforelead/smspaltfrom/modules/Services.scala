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
    val smsTemplates = SMSTemplates[F](redis)
    val messages     = Messages[F]

    new Services[F](
      users = Users[F],
      contacts = contacts,
      holidays = Holidays[F],
      messages = messages,
      smsTemplates = smsTemplates,
      templateCategories = TemplateCategories[F],
      congratulator = Congratulator.make[F](contacts, smsTemplates, messages, redis)
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
