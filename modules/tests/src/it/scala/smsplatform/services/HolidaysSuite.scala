package smsplatform.services

import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.itforelead.smspaltfrom.domain.Holiday.{UpdateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.services.{Holidays, SMSTemplates, TemplateCategories}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createHolidayGen, createSMSTemplateGen, createTemplateCategoryGen, holidayNameGen}

object HolidaysSuite extends DBSuite {

  test("Create Holiday") { implicit postgres =>
    val holidays = Holidays[IO]
    forall(createHolidayGen) { createHoliday =>
      for {
        holiday1 <- holidays.create(createHoliday)
        holiday2 <- holidays.holidays
      } yield assert(holiday2.contains(holiday1))
    }
  }

  test("Update Holiday") { implicit postgres =>
    val holidays = Holidays[IO]

    val gen = for {
      c <- createHolidayGen
      t <- holidayNameGen
    } yield (c, t)
    forall(gen) { case (createHoliday, name) =>
      for {
        holiday1 <- holidays.create(createHoliday)
        holiday2 <- holidays.update(
          UpdateHoliday(
            id = holiday1.id,
            name = name,
            day = holiday1.day,
            month = holiday1.month
          )
        )
      } yield assert.same(holiday2.name, name)
    }
  }

  test("Update TemplateID in Holiday") { implicit postgres =>
    val holidays           = Holidays[IO]
    val templates          = SMSTemplates[IO](RedisClient)
    val templateCategories = TemplateCategories[IO]

    val gen = for {
      c  <- createHolidayGen
      tc <- createTemplateCategoryGen
      t1 <- createSMSTemplateGen
      t2 <- createSMSTemplateGen
    } yield (c, tc, t1, t2)

    forall(gen) { case (createHoliday, createTmplCat, createTemplate1, createTemplate2) =>
      for {
        holiday1     <- holidays.create(createHoliday)
        tmplCategory <- templateCategories.create(createTmplCat)
        template1    <- templates.create(createTemplate1.copy(templateCategoryId = tmplCategory.id))
        template2    <- templates.create(createTemplate2.copy(templateCategoryId = tmplCategory.id))
        holiday2 <- holidays.updateTemplateInHoliday(
          UpdateTemplateInHoliday(
            id = holiday1.id,
            smsWomenId = template1.id.some,
            smsMenId = template2.id.some
          )
        )
      } yield assert(holiday2.smsMenId.contains(template2.id) && holiday2.smsWomenId.contains(template1.id))
    }
  }

  test("Delete Holiday") { implicit postgres =>
    val holidays = Holidays[IO]
    forall(createHolidayGen) { createHoliday =>
      for {
        holiday1 <- holidays.create(createHoliday)
        _        <- holidays.delete(holiday1.id)
        holiday3 <- holidays.holidays
      } yield assert(!holiday3.contains(holiday1))
    }
  }

}
