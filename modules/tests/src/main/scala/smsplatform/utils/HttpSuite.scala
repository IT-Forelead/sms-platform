package smsplatform.utils

import cats.data.OptionT
import cats.effect.IO
import dev.profunktor.auth.JwtAuthMiddleware
import dev.profunktor.auth.jwt.{JwtAuth, JwtToken}
import eu.timepit.refined.auto.autoUnwrap
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import pdi.jwt.{JwtAlgorithm, JwtClaim}
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.implicits.CirceDecoderOps
import com.itforelead.smspaltfrom.services.redis.RedisClient
import smsplatform.stub_services.{AuthMock, RedisClientMock}
import smsplatform.config.jwtConfig
import weaver.scalacheck.Checkers
import weaver.{Expectations, SimpleIOSuite}

import scala.concurrent.duration.DurationInt

trait HttpSuite extends SimpleIOSuite with Checkers {

  val RedisClient: RedisClient[IO] = RedisClientMock[IO]

  def findUser(token: JwtToken): JwtClaim => F[Option[User]] = _ =>
    OptionT(RedisClient.get(token.value))
      .map(_.as[User])
      .value

  protected val usersMiddleware: AuthMiddleware[F, User] =
    JwtAuthMiddleware[F, User](JwtAuth.hmac(jwtConfig.tokenConfig.value.secret, JwtAlgorithm.HS256), findUser)

  def authToken(user: User): IO[Authorization] =
    for {
      token <- AuthMock.tokens[IO].flatMap(_.create)
      _     <- RedisClient.put(token.value, user, 1.minute)
    } yield Authorization(Credentials.Token(AuthScheme.Bearer, token.value))

  def expectHttpBodyAndStatus[A: Encoder](routes: HttpRoutes[IO], req: Request[IO])(
    expectedBody: A,
    expectedStatus: Status
  ): IO[Expectations] =
    routes.run(req).value.flatMap {
      case Some(resp) =>
        resp.asJson.map { json =>
          expect.all(resp.status == expectedStatus, json.dropNullValues == expectedBody.asJson.dropNullValues)
        }
      case None => IO.pure(failure("route not found"))
    }

  def expectHttpStatus(routes: HttpRoutes[IO], req: Request[IO])(expectedStatus: Status): IO[Expectations] =
    routes.run(req).value.map {
      case Some(resp) => expect.same(resp.status, expectedStatus)
      case None       => failure("route not found")
    }

  def expectHttpFailure(routes: HttpRoutes[IO], req: Request[IO]): IO[Expectations] =
    routes.run(req).value.attempt.map {
      case Left(_)  => success
      case Right(_) => failure("expected a failure")
    }

}
