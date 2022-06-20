package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.domain.SystemSetting.UpdateTemplateOfBirthday
import com.itforelead.smspaltfrom.routes.{SystemSettingRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.SystemSettings
import org.http4s.Method.{GET, PUT}
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.SystemSettingsStub
import smsplatform.utils.Generators._
import smsplatform.utils.HttpSuite

object SystemSettingRoutesSuite extends HttpSuite {

  def systemSettings[F[_]: Sync](systemSettings: SystemSetting): SystemSettings[F] = new SystemSettingsStub[F] {
    override def settings: F[Option[SystemSetting]]            = Sync[F].delay(Option(systemSettings))
    override def update(form: SystemSetting): F[SystemSetting] = Sync[F].delay(systemSettings)
    override def updateTemplateOfBirthday(form: UpdateTemplateOfBirthday): F[SystemSetting] = Sync[F].delay(systemSettings)
  }

  test("get setting") {
    val gen = for {
      u <- userGen
      s <- systemSettingsGen
    } yield (u, s)

    forall(gen) { case (user, settings) =>
      for {
        token <- authToken(user)
        req    = GET(uri"/settings").putHeaders(token)
        routes = SystemSettingRoutes[IO](systemSettings(settings)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

  test("update setting") {
    val gen = for {
      u  <- userGen
      s  <- systemSettingsGen
      us <- systemSettingsGen

    } yield (u, s, us)

    forall(gen) { case (user, settings, updateSetting) =>
      for {
        token <- authToken(user)
        req    = PUT(updateSetting, uri"/settings").putHeaders(token)
        routes = SystemSettingRoutes[IO](systemSettings(settings)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(settings, Status.Ok)
      } yield res
    }
  }

  test("update template birthday") {
    val gen = for {
      u  <- userGen
      s  <- systemSettingsGen
      us <- updateTemplateOfBirthdayGen

    } yield (u, s, us)

    forall(gen) { case (user, settings, updateSetting) =>
      for {
        token <- authToken(user)
        req    = PUT(updateSetting, uri"/settings/update-template").putHeaders(token)
        routes = SystemSettingRoutes[IO](systemSettings(settings)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(settings, Status.Ok)
      } yield res
    }
  }

}
