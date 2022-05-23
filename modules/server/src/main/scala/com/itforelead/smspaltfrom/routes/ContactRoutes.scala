package com.itforelead.smspaltfrom.routes

import cats.implicits._
import cats.MonadThrow
import com.itforelead.smspaltfrom.domain.Contact.CreateContact
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.services.Contacts
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final class ContactRoutes[F[_]: JsonDecoder: MonadThrow](
  contacts: Contacts[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/contact"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of { case aR @ POST -> Root as _ =>
    aR.req.decodeR[CreateContact] { form =>
      contacts
        .create(form)
        .flatMap(Created(_))
        .handleErrorWith { err =>
          logger.error(s"Error occurred while create contact. Error $err") >>
            BadRequest("Xatolik yuz berdi!")
        }
    }

  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
