package smsplatform.e2e

import cats.effect.unsafe.implicits.global
import com.itforelead.smspaltfrom.domain.{SMSTemplate, TemplateCategory, tokenDecoder}
import com.itforelead.smspaltfrom.routes.{deriveEntityDecoder, deriveEntityEncoder}
import dev.profunktor.auth.jwt.JwtToken
import org.http4s.Method.POST
import org.http4s.client.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.implicits._
import org.http4s.{AuthScheme, Credentials}
import smsplatform.utils.ClientSuite
import smsplatform.utils.Generators.{createSMSTemplateGen, createTemplateCategoryGen}

object SMSTemplateRoutesSuite extends ClientSuite {

  test("create sms template") { implicit client =>
    val gen =
      for {
        tc <- createTemplateCategoryGen
        t  <- createSMSTemplateGen
      } yield tc -> t
    val token = loginReq.expectAs[JwtToken].unsafeRunSync()
    forall(gen) { case newTempCategory -> newTemplate =>
      for {
        templateCategory <- POST(newTempCategory, uri"/template-category")
          .putHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
          .expectAs[TemplateCategory]
        template <- POST(newTemplate.copy(templateCategoryId = templateCategory.id), uri"/sms-template")
          .putHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, token.value)))
          .expectAs[SMSTemplate]
      } yield assert.same(template.templateCategoryId, templateCategory.id)
    }
  }

}
