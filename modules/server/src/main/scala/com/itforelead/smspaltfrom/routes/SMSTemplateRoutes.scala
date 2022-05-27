package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.SMSTemplate.CreateSMSTemplate
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.services.SMSTemplates
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final case class SMSTemplateRoutes[F[_]: JsonDecoder: MonadThrow](
  templates: SMSTemplates[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/sms-template"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case aR @ POST -> Root as _ =>
      aR.req.decodeR[CreateSMSTemplate] { form =>
        templates.create(form).flatMap(Created(_))
      }

    case GET -> Root as _ =>
      templates.templates.flatMap(Ok(_))
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
