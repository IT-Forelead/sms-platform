package com.itforelead.smspaltfrom.routes

import cats.syntax.all._
import cats.{Monad, MonadThrow}
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.domain.User.CreateUser
import com.itforelead.smspaltfrom.services.Auth
import dev.profunktor.auth.AuthHeaders
import io.circe.refined.refinedEncoder
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import com.itforelead.smspaltfrom.domain
import com.itforelead.smspaltfrom.domain.custom.exception.{EmailInUse, InvalidPassword, UserNotFound}
import com.itforelead.smspaltfrom.domain.tokenEncoder

final case class AuthRoutes[F[_]: Monad: JsonDecoder: MonadThrow](
  auth: Auth[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/auth"

  private val publicRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "login" =>
      req.decodeR[domain.Credentials] { credentials =>
        auth
          .login(credentials)
          .flatMap(Ok(_))
          .recoverWith { case UserNotFound(_) | InvalidPassword(_) =>
            Forbidden()
          }
      }
    case req @ POST -> Root / "user" =>
      req.decodeR[CreateUser] { user =>
        auth
          .newUser(user)
          .flatMap(Created(_))
          .recoverWith { case EmailInUse(u) =>
            Conflict(u)
          }
      }
  }
  private[this] val privateRoutes: AuthedRoutes[User, F] = AuthedRoutes.of { case ar @ GET -> Root / "logout" as user =>
    AuthHeaders
      .getBearerToken(ar.req)
      .traverse_(auth.logout(_, user.email)) *> NoContent()
  }

  def routes(authMiddleware: AuthMiddleware[F, User]): HttpRoutes[F] = Router(
    prefixPath -> (publicRoutes <+> authMiddleware(privateRoutes))
  )

}
