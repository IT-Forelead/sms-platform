package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.SMSTemplate.CreateSMSTemplate
import com.itforelead.smspaltfrom.routes.{SMSTemplateRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.SMSTemplates
import org.http4s.Method.{GET, POST}
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.SMSTemplatesStub
import smsplatform.utils.Generators.{createSMSTemplateGen, smsTemplateGen, userGen}
import smsplatform.utils.HttpSuite

object SMSTemplateRoutesSuite extends HttpSuite {

  def smsTemplates[F[_]: Sync](from: SMSTemplate): SMSTemplates[F] = new SMSTemplatesStub[F] {
    override def create(form: CreateSMSTemplate): F[SMSTemplate] = Sync[F].delay(from)
    override def templates: F[List[SMSTemplate]] = Sync[F].delay(List(from))
  }

  test("create sms template") {
    val gen = for {
      u  <- userGen
      s  <- smsTemplateGen
      cs <- createSMSTemplateGen
    } yield (u, s, cs)

    forall(gen) { case (user, template, newTemplate) =>
      for {
        token <- authToken(user)
        req    = POST(newTemplate, uri"/sms-template").putHeaders(token)
        routes = SMSTemplateRoutes[IO](smsTemplates(template)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(template, Status.Created)
      } yield res
    }
  }

  test("get sms templates") {
    val gen = for {
      u  <- userGen
      s  <- smsTemplateGen
    } yield (u, s)

    forall(gen) { case (user, template) =>
      for {
        token <- authToken(user)
        req    = GET(uri"/sms-template").putHeaders(token)
        routes = SMSTemplateRoutes[IO](smsTemplates(template)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

}
