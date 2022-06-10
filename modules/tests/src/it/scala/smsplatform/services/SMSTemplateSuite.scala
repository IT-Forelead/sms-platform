package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.services.{SMSTemplates, TemplateCategories}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createSMSTemplateGen, createTemplateCategoryGen, titleGen}

object SMSTemplateSuite extends DBSuite {

  test("Create SMS Template") { implicit postgres =>
    val templates          = SMSTemplates[IO](RedisClient)
    val templateCategories = TemplateCategories[IO]
    val gen = for {
      c <- createTemplateCategoryGen
      t <- createSMSTemplateGen
    } yield (c, t)
    forall(gen) { case (c, createTemplate) =>
      for {
        templateCategory <- templateCategories.create(c)
        template1        <- templates.create(createTemplate.copy(templateCategoryId = templateCategory.id))
        template2        <- templates.templates
      } yield assert(template2.contains(template1))
    }
  }

  test("Update SMS Template") { implicit postgres =>
    val smsTemplates       = SMSTemplates[IO](RedisClient)
    val templateCategories = TemplateCategories[IO]
    val gen = for {
      t  <- createTemplateCategoryGen
      c  <- createSMSTemplateGen
      ct <- titleGen
    } yield (t, c, ct)
    forall(gen) { case (createTemplateCategory, createSMSTemplate, title) =>
      for {
        templateCategory <- templateCategories.create(createTemplateCategory)
        template1        <- smsTemplates.create(createSMSTemplate.copy(templateCategoryId = templateCategory.id))
        template2 <- smsTemplates.update(
          SMSTemplate(
            id = template1.id,
            templateCategoryId = template1.templateCategoryId,
            title = title,
            text = template1.text,
            gender = template1.gender
          )
        )
      } yield assert.same(template2.title, title)
    }
  }

  test("Delete SMS Template") { implicit postgres =>
    val smsTemplates       = SMSTemplates[IO](RedisClient)
    val templateCategories = TemplateCategories[IO]
    val gen = for {
      t <- createTemplateCategoryGen
      c <- createSMSTemplateGen
    } yield (t, c)
    forall(gen) { case (createTemplateCategory, createSMSTemplate) =>
      for {
        templateCategory <- templateCategories.create(createTemplateCategory)
        template1        <- smsTemplates.create(createSMSTemplate.copy(templateCategoryId = templateCategory.id))
        _                <- smsTemplates.delete(template1.id)
        template2        <- smsTemplates.templates
      } yield assert(!template2.contains(template1))
    }
  }
}
