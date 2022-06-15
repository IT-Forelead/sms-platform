package smsplatform.services

import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.itforelead.smspaltfrom.domain.Holiday.UpdateTemplateInHoliday
import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.services.{Holidays, SMSTemplates}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createHolidayGen, holidayNameGen, templateIdOptionGen}

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
          Holiday(
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
    val holidays  = Holidays[IO]

    val gen = for {
      c <- createHolidayGen
      u1 <- templateIdOptionGen
      u2 <- templateIdOptionGen
    } yield (c, u1, u2)

    forall(gen) { case (createHoliday, templateIdOption1, templateIdOption2) =>
      for {
        holiday1 <- holidays.create(createHoliday)
        holiday2 <- holidays.updateTemplateInHoliday(
          UpdateTemplateInHoliday(
            id = holiday1.id,
            smsWomenId = templateIdOption1,
            smsMenId = templateIdOption2
          )
        )
      } yield assert.same((holiday2.smsMenId, templateIdOption2), (holiday2.smsWomenId, templateIdOption1))
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
