package smsplatform.services

import cats.effect.IO
import cats.implicits.catsSyntaxOptionId
import com.itforelead.smspaltfrom.domain.Holiday.CreateHoliday
import com.itforelead.smspaltfrom.domain.{Gender, Holiday, SMSTemplate}
import com.itforelead.smspaltfrom.services.{Holidays, SMSTemplates}
import smsplatform.services.SMSTemplateSuite.RedisClient
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createHolidayGen, createSMSTemplateGen, holidayNameGen, smsAllIdGen, smsTemplateGen}

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
    val holidays  = Holidays[IO]
    val templates = SMSTemplates[IO](RedisClient)
    def setTempId(holiday: CreateHoliday, template: SMSTemplate) =
      template.gender match {
        case Gender.ALL    => holiday.copy(smsAllId = template.id.some)
        case Gender.MALE   => holiday.copy(smsMenId = template.id.some)
        case Gender.FEMALE => holiday.copy(smsWomenId = template.id.some)
      }

    val gen = for {
      t <- holidayNameGen
      c <- createHolidayGen
      s <- createSMSTemplateGen
    } yield (c, t, s)
    forall(gen) { case (createHoliday, name, smsTemplate) =>
      for {
        template <- templates.create(smsTemplate)
        holiday1 <- holidays.create(setTempId(createHoliday, template))
        holiday2 <- holidays.update(
          Holiday(
            id = holiday1.id,
            name = name,
            day = holiday1.day,
            month = holiday1.month,
            smsWomenId = holiday1.smsWomenId,
            smsMenId = holiday1.smsMenId,
            smsAllId = holiday1.smsAllId
          )
        )
      } yield assert.same(holiday2.name, name)
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
