package smsplatform.services

import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.domain.SystemSetting.UpdateTemplateOfBirthday
import com.itforelead.smspaltfrom.services.{SMSTemplates, SystemSettings, TemplateCategories}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createSMSTemplateGen, createTemplateCategoryGen}

object SettingsSuite extends DBSuite {

  test("Update settings") { implicit postgres =>
    val settings = SystemSettings[IO]

    for {
      settings1 <- settings.settings
      settings2 <- settings.update(
        SystemSetting(
          autoSendBirthday = settings1.fold(false)(_.autoSendBirthday),
          autoSendHoliday = settings1.fold(false)(_.autoSendHoliday),
          darkTheme = true
        )
      )
    } yield assert.same(true, settings2.darkTheme)
  }

  test("Update Template of Birthday") { implicit postgres =>
    val templates          = SMSTemplates[IO]
    val templateCategories = TemplateCategories[IO]
    val settings           = SystemSettings[IO]

    val gen = for {
      tc <- createTemplateCategoryGen
      t1 <- createSMSTemplateGen
      t2 <- createSMSTemplateGen
    } yield (tc, t1, t2)

    forall(gen) { case (createTmplCat, createTemplate1, createTemplate2) =>
      for {
        tmplCategory <- templateCategories.create(createTmplCat)
        template1    <- templates.create(createTemplate1.copy(templateCategoryId = tmplCategory.id))
        template2    <- templates.create(createTemplate2.copy(templateCategoryId = tmplCategory.id))
        settings1 <- settings.updateTemplateOfBirthday(
          UpdateTemplateOfBirthday(
            smsMenId = template2.id.some,
            smsWomenId = template1.id.some
          )
        )
      } yield assert(settings1.smsMenId.contains(template2.id) && settings1.smsWomenId.contains(template1.id))
    }
  }

}
