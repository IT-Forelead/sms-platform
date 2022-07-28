package com.itforelead.smspaltfrom.routes

import cats.MonadThrow
import cats.implicits._
import com.itforelead.smspaltfrom.domain.TemplateCategory.{CreateTemplateCategory, UpdateTemplateCategory}
import com.itforelead.smspaltfrom.domain.types.TemplateCategoryId
import com.itforelead.smspaltfrom.domain.{TemplateCategory, User}
import com.itforelead.smspaltfrom.services.TemplateCategories
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.log4cats.Logger

final case class TemplateCategoryRoutes[F[_]: JsonDecoder: MonadThrow](
  categories: TemplateCategories[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F] {

  private[routes] val prefixPath = "/template-category"

  private[this] val httpRoutes: AuthedRoutes[User, F] = AuthedRoutes.of {
    case aR @ POST -> Root as user =>
      aR.req.decodeR[CreateTemplateCategory] { form =>
        categories.create(user.id, form).flatMap(Created(_))
      }

    case GET -> Root as user =>
      categories.templateCategories(user.id).flatMap(Ok(_))

    case aR @ PUT -> Root as user =>
      aR.req.decodeR[UpdateTemplateCategory] { form =>
        categories.update(user.id, form).flatMap(Ok(_))
      }

    case aR @ DELETE -> Root as user =>
      aR.req.decodeR[TemplateCategoryId] { id =>
        categories.delete(id, user.id) >> NoContent()
      }
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
