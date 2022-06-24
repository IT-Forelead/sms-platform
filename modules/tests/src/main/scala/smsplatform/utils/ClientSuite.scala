package smsplatform.utils

import cats.effect.std.Supervisor
import cats.effect.{Async, IO, Resource}
import cats.implicits._
import ciris.Secret
import com.itforelead.smspaltfrom.config.{AppConfig, ConfigLoader}
import com.itforelead.smspaltfrom.domain.AppEnv.TEST
import com.itforelead.smspaltfrom.domain.Credentials
import com.itforelead.smspaltfrom.domain.custom.refinements.{EmailAddress, Password}
import com.itforelead.smspaltfrom.effects.Background
import com.itforelead.smspaltfrom.modules.{HttpApi, Security, Services}
import com.itforelead.smspaltfrom.resources.AppResources
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.types.all.NonSystemPortNumber
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.POST
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Status, _}
import skunk.Session
import weaver.scalacheck.{CheckConfig, Checkers}
import weaver.{Expectations, IOSuite}

trait ClientSuite extends IOSuite with Checkers with Container {
  type Res = Client[IO]
  override def checkConfig: CheckConfig = customCheckConfig

  val email: EmailAddress = EmailAddress.unsafeFrom("admin@gmail.com")
  val password: Password  = Password.unsafeFrom("Secret1!")

  def application(config: AppConfig)(implicit ev: Background[IO]): Resource[IO, HttpApp[IO]] =
    AppResources[IO](config)
      .evalMap { res =>
        implicit val session: Resource[IO, Session[IO]] = res.postgres

        val services = Services[IO](config.messageBroker, config.scheduler, res.httpClient)
        Security[IO](config, services.users, res.redis).map { security =>
          HttpApi[IO](security, services, res.redis, config.logConfig).httpApp
        }
      }

  private def httpAppRes: Resource[IO, HttpApp[IO]] = {
    for {
      container <- dbResource

      httpApp <- Supervisor[IO].flatMap { implicit sp =>
        Resource.eval(ConfigLoader.load[IO]).flatMap { cfg =>
          application {
            if (cfg.env == TEST)
              cfg.copy(dbConfig =
                cfg.dbConfig.copy(
                  host = NonEmptyString.unsafeFrom(container.getHost),
                  port = NonSystemPortNumber.unsafeFrom(container.getFirstMappedPort),
                  user = NonEmptyString.unsafeFrom(container.getUsername),
                  password = Secret(NonEmptyString.unsafeFrom(container.getPassword)),
                  database = NonEmptyString.unsafeFrom(container.getDatabaseName)
                )
              )
            else cfg
          }
        }
      }
    } yield httpApp

  }

  def loginReq: Request[IO] =
    POST(Credentials(email, password), uri"/auth/login")

  override def sharedResource: Resource[IO, Res] =
    httpAppRes.map(Client.fromHttpApp[IO])

  implicit class RequestOps[F[_]: Async](request: Request[F]) {
    def expectHttpStatus(status: Status)(implicit
      client: Client[F]
    ): F[Expectations] =
      client.status(request).map { found =>
        expect.same(found, status)
      }

    def expectAs[A](implicit client: Client[F], ev: EntityDecoder[F, A]): F[A] =
      client.expect[A](request)
  }

}
