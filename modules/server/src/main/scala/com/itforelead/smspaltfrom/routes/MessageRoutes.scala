package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.services.Messages
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final case class MessageRoutes[F[_]: JsonDecoder: MonadThrow](
  messages: Messages[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/messages"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of { case GET -> Root as user =>
    messages.messages(user.id).flatMap(Ok(_))
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
