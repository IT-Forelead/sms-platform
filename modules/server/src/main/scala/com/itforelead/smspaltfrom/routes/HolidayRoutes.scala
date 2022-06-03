package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Holiday.CreateHoliday
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
    case aR @ POST -> Root as _ =>
      aR.req.decodeR[CreateHoliday] { form =>
        holidays.create(form).flatMap(Created(_))
      }

    case GET -> Root as _ =>
      holidays.contacts.flatMap(Ok(_))

    case aR @ PUT -> Root as _ =>
      aR.req.decodeR[Holiday] { from =>
        holidays.update(from).flatMap(Ok(_))
      }

    case aR @ DELETE -> Root as _ =>
      aR.req.decodeR[HolidayId] { id =>
        holidays.delete(id) >> NoContent()
      }
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
