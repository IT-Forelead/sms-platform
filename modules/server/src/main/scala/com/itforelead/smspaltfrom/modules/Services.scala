package com.itforelead.smspaltfrom.modules

import cats.effect.{Resource, Sync}
import skunk.Session
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.services.Users

object Services {
  def apply[F[_]: Sync: GenUUID](implicit session: Resource[F, Session[F]]) =
    new Services[F](
      users = Users[F]
    )
}

final class Services[F[_]] private (
  val users: Users[F]
)
