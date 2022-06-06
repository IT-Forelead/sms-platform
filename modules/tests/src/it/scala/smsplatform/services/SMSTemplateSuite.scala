package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.domain.types.TemplateCategoryId
import com.itforelead.smspaltfrom.services.SMSTemplates
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.createSMSTemplateGen

import java.util.UUID

object SMSTemplateSuite extends DBSuite {

  test("Create Template") { implicit postgres =>
    val templates = SMSTemplates[IO]
    forall(createSMSTemplateGen) { createTemplate =>
        for {
          template1 <- templates.create(createTemplate.copy(templateCategoryId = TemplateCategoryId(UUID.fromString("c1039d34-425b-4f78-9a7f-893f5b4ac478"))))
          template2 <- templates.templates
        } yield assert(template2.contains(template1))
    }
  }

//  test("Update Template") { implicit postgres =>
//    val templates = SMSTemplates[IO]
//    forall(updateSMSTemplateGen) { updateTemplate =>
//      for {
//        template1 <- templates.update(updateTemplate)
//        template2 <- templates.templates
//      } yield assert(template2.contains(template1))
//    }
//  }
}
