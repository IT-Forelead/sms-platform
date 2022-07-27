package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.SMSTemplate.{CreateSMSTemplate, SMSTemplateWithCatName, UpdateSMSTemplate}
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryName, TemplateId, UserId}
import com.itforelead.smspaltfrom.routes.{SMSTemplateRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.SMSTemplates
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.{DELETE, GET, POST, PUT}
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.SMSTemplatesStub
import smsplatform.utils.Generators.{createSMSTemplateGen, smsTemplateGen, templateIdGen, updateSMSTemplateGen, userGen}
import smsplatform.utils.HttpSuite

object SMSTemplateRoutesSuite extends HttpSuite {

  def smsTemplates[F[_]: Sync](from: SMSTemplate): SMSTemplates[F] = new SMSTemplatesStub[F] {
    override def create(userId: UserId, form: CreateSMSTemplate): F[SMSTemplate] = Sync[F].delay(from)
    override def templates(userId: UserId): F[List[SMSTemplateWithCatName]] = Sync[F].delay(
      List(
        SMSTemplateWithCatName(
          id = from.id,
          userId = from.userId,
          templateCategoryId = from.templateCategoryId,
          title = from.title,
          text = from.text,
          genderAccess = from.gender,
          categoryName = TemplateCategoryName(NonEmptyString.unsafeFrom("email"))
        )
      )
    )
    override def update(userId: UserId, form: UpdateSMSTemplate): F[SMSTemplate] = Sync[F].delay(from)
    override def delete(id: TemplateId, userId: UserId): F[Unit]           = Sync[F].unit
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
      u <- userGen
      s <- smsTemplateGen
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

  test("update sms template") {
    val gen = for {
      u  <- userGen
      s  <- smsTemplateGen
      us <- updateSMSTemplateGen
    } yield (u, s, us)

    forall(gen) { case (user, template, newTemplate) =>
      for {
        token <- authToken(user)
        req    = PUT(newTemplate, uri"/sms-template").putHeaders(token)
        routes = SMSTemplateRoutes[IO](smsTemplates(template)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(template, Status.Ok)
      } yield res
    }
  }

  test("delete sms template") {
    val gen = for {
      u <- userGen
      s <- smsTemplateGen
      i <- templateIdGen
    } yield (u, s, i)

    forall(gen) { case (user, template, templateId) =>
      for {
        token <- authToken(user)
        req    = DELETE(templateId, uri"/sms-template").putHeaders(token)
        routes = SMSTemplateRoutes[IO](smsTemplates(template)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.NoContent)
      } yield res
    }
  }

}
