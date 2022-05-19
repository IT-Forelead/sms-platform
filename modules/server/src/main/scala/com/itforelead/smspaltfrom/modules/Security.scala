package com.itforelead.smspaltfrom.modules

import cats.effect._
import cats.syntax.all._
import dev.profunktor.auth.jwt._
import eu.timepit.refined.auto._
import pdi.jwt._
import skunk.Session
import com.itforelead.smspaltfrom.config.AppConfig
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.domain.types.UserJwtAuth
import com.itforelead.smspaltfrom.resources.AppResources
import com.itforelead.smspaltfrom.security.{Crypto, JwtExpire, Tokens}
import com.itforelead.smspaltfrom.services.redis.RedisClient
import com.itforelead.smspaltfrom.services.{Auth, Users, UsersAuth}
object Security {
  def apply[F[_]: Sync](
    cfg: AppConfig,
    users: Users[F],
    redis: RedisClient[F]
  ): F[Security[F]] = {

    for {
      tokens <- JwtExpire[F].map(Tokens.make[F](_, cfg.jwtConfig.tokenConfig.value, cfg.jwtConfig.tokenExpiration))
      userJwtAuth = UserJwtAuth(JwtAuth.hmac(cfg.jwtConfig.tokenConfig.value.secret, JwtAlgorithm.HS256))
      auth        = Auth[F](cfg.jwtConfig.tokenExpiration, tokens, users, redis)
    } yield new Security[F](auth, userJwtAuth)

  }
}

final class Security[F[_]] private (
  val auth: Auth[F],
  val userJwtAuth: UserJwtAuth
)
