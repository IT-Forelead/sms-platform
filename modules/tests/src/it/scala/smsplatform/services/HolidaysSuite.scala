package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.services.Holidays
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createHolidayGen, holidayNameGen}

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
      t <- holidayNameGen
      c <- createHolidayGen
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
