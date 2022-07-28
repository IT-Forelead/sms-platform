package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.Holiday.{CreateHoliday, UpdateHoliday}
import com.itforelead.smspaltfrom.domain.types.{HolidayId, UserId}
import com.itforelead.smspaltfrom.routes.{HolidayRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.Holidays
import org.http4s.Method.{DELETE, GET, POST, PUT}
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.HolidaysStub
import smsplatform.utils.Generators._
import smsplatform.utils.HttpSuite

object HolidayRoutesSuite extends HttpSuite {

  def holidays[F[_]: Sync](holiday: Holiday): Holidays[F] = new HolidaysStub[F] {
    override def create(userId: UserId, form: CreateHoliday): F[Holiday] = Sync[F].delay(holiday)
    override def holidays(userId: UserId): F[List[Holiday]]              = Sync[F].delay(List(holiday))
    override def update(userId: UserId, form: UpdateHoliday): F[Holiday] = Sync[F].delay(holiday)
    override def delete(id: HolidayId, userId: UserId): F[Unit]          = Sync[F].unit
  }

  test("create holiday") {
    val gen = for {
      u  <- userGen
      ch <- createHolidayGen
      h  <- holidayGen
    } yield (u, h, ch)

    forall(gen) { case (user, holiday, newHoliday) =>
      for {
        token <- authToken(user)
        req    = POST(newHoliday, uri"/holiday").putHeaders(token)
        routes = HolidayRoutes[IO](holidays(holiday)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(holiday, Status.Created)
      } yield res
    }
  }

  test("get holiday") {
    val gen = for {
      u <- userGen
      h <- holidayGen
    } yield (u, h)

    forall(gen) { case (user, holiday) =>
      for {
        token <- authToken(user)
        req    = GET(uri"/holiday").putHeaders(token)
        routes = HolidayRoutes[IO](holidays(holiday)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

  test("update holiday") {
    val gen = for {
      u  <- userGen
      h  <- holidayGen
      uh <- updateHolidayGen
    } yield (u, h, uh)

    forall(gen) { case (user, holiday, updateHoliday) =>
      for {
        token <- authToken(user)
        req    = PUT(updateHoliday, uri"/holiday").putHeaders(token)
        routes = HolidayRoutes[IO](holidays(holiday)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(holiday, Status.Ok)
      } yield res
    }
  }

  test("delete contact") {
    val gen = for {
      u <- userGen
      h <- holidayGen
      i <- holidayIdGen
    } yield (u, h, i)

    forall(gen) { case (user, holiday, holidayId) =>
      for {
        token <- authToken(user)
        req    = DELETE(holidayId, uri"/holiday").putHeaders(token)
        routes = HolidayRoutes[IO](holidays(holiday)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.NoContent)
      } yield res
    }
  }

}
