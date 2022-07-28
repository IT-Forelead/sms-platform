package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Holiday.{CreateHoliday, UpdateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.domain.{Holiday, User}
import com.itforelead.smspaltfrom.domain.types.HolidayId
import com.itforelead.smspaltfrom.services.Holidays
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final case class HolidayRoutes[F[_]: JsonDecoder: MonadThrow](
  holidays: Holidays[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/holiday"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case aR @ POST -> Root as user =>
      aR.req.decodeR[CreateHoliday] { form =>
        holidays.create(user.id, form).flatMap(Created(_))
      }

    case GET -> Root as user =>
      holidays.holidays(user.id).flatMap(Ok(_))

    case aR @ PUT -> Root as user =>
      aR.req.decodeR[UpdateHoliday] { from =>
        holidays.update(user.id, from).flatMap(Ok(_))
      }

    case aR @ PUT -> Root / "update-template" as user =>
      aR.req.decodeR[UpdateTemplateInHoliday] { from =>
        holidays.updateTemplateInHoliday(user.id, from).flatMap(Ok(_))
      }

    case aR @ DELETE -> Root as user =>
      aR.req.decodeR[HolidayId] { id =>
        holidays.delete(id, user.id) >> NoContent()
      }
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
