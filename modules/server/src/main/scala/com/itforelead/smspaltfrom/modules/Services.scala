package com.itforelead.smspaltfrom.modules

import cats.effect.{Resource, Sync}
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.services.{Congratulator, Contacts, SMSTemplates, Users}
import skunk.Session

object Services {
  def apply[F[_]: Sync: GenUUID](implicit session: Resource[F, Session[F]]): Services[F] = {
    val contacts = Contacts[F]
    new Services[F](
      users = Users[F],
      contacts = contacts,
      smsTemplates = SMSTemplates[F],
      congratulator = Congratulator.make[F](contacts)
    )
  }
}

final class Services[F[_]] private (
  val users: Users[F],
  val contacts: Contacts[F],
  val smsTemplates: SMSTemplates[F],
  val congratulator: Congratulator[F]
)
