package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.SystemSetting.UpdateTemplateOfBirthday
import com.itforelead.smspaltfrom.domain.{SystemSetting, User}
import com.itforelead.smspaltfrom.services.SystemSettings
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final case class SystemSettingRoutes[F[_]: JsonDecoder: MonadThrow](
  systemSettings: SystemSettings[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/setting"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {

    case GET -> Root as _ =>
      systemSettings.settings.flatMap(Ok(_))

    case aR @ PUT -> Root as _ =>
      aR.req.decodeR[SystemSetting] { from =>
        systemSettings.update(from).flatMap(Ok(_))
      }

    case aR @ PUT -> Root / "update-template" as _ =>
      aR.req.decodeR[UpdateTemplateOfBirthday] { from =>
        systemSettings.updateTemplateOfBirthday(from).flatMap(Ok(_))
      }

  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
