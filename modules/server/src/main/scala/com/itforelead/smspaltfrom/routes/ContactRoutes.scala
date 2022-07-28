package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Contact.{CreateContact, UpdateContact}
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.domain.types.ContactId
import com.itforelead.smspaltfrom.services.Contacts
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final case class ContactRoutes[F[_]: JsonDecoder: MonadThrow](
  contacts: Contacts[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/contact"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case aR @ POST -> Root as user =>
      aR.req.decodeR[CreateContact] { form =>
        contacts.create(user.id, form).flatMap(Created(_))
      }

    case GET -> Root as user =>
      contacts.contacts(user.id).flatMap(Ok(_))

    case aR @ PUT -> Root as user =>
      aR.req.decodeR[UpdateContact] { from =>
        contacts.update(user.id, from).flatMap(Ok(_))
      }

    case aR @ DELETE -> Root as user =>
      aR.req.decodeR[ContactId] { id =>
        contacts.delete(id, user.id) >> NoContent()
      }
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
