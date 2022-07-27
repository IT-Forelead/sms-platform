package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.SMSTemplate.{CreateSMSTemplate, UpdateSMSTemplate}
import com.itforelead.smspaltfrom.domain.types.TemplateId
import com.itforelead.smspaltfrom.domain.{SMSTemplate, User}
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
    case aR @ POST -> Root as user =>
      aR.req.decodeR[CreateSMSTemplate] { form =>
        templates.create(user.id, form).flatMap(Created(_))
      }

    case GET -> Root as user =>
      templates.templates(user.id).flatMap(Ok(_))

    case aR @ PUT -> Root as user =>
      aR.req.decodeR[UpdateSMSTemplate] { form =>
        templates.update(user.id, form).flatMap(Ok(_))
      }

    case aR @ DELETE -> Root as user =>
      aR.req.decodeR[TemplateId] { id =>
        templates.delete(id, user.id) >> NoContent()
      }
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
