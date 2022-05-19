package smsplatform.stub_services

import cats.effect.Sync
import cats.implicits._
import com.itforelead.smspaltfrom.security.{JwtExpire, Tokens}
import com.itforelead.smspaltfrom.services.redis.RedisClient
import smsplatform.config.jwtConfig
import com.itforelead.smspaltfrom.services.{Auth, Users}

object AuthMock {
  def tokens[F[_]: Sync]: F[Tokens[F]] =
    JwtExpire[F].map(Tokens.make[F](_, jwtConfig.tokenConfig.value, jwtConfig.tokenExpiration))

  def apply[F[_]: Sync](users: Users[F], redis: RedisClient[F]): F[Auth[F]] =
    for {
      tokens <- tokens
      auth = Auth[F](jwtConfig.tokenExpiration, tokens, users, redis)
    } yield auth
}
