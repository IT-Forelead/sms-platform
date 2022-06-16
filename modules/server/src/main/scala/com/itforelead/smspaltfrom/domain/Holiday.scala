package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.custom.refinements.DayOfMonth
import com.itforelead.smspaltfrom.domain.types.{HolidayId, HolidayName, TemplateId}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._
import eu.timepit.refined.cats._

@derive(decoder, encoder, show)
case class Holiday(
  id: HolidayId,
  name: HolidayName,
  day: DayOfMonth,
  month: Month,
  smsWomenId: Option[TemplateId] = None,
  smsMenId: Option[TemplateId] = None
)

object Holiday {
  @derive(decoder, encoder, show)
  case class CreateHoliday(
    name: HolidayName,
    day: DayOfMonth,
    month: Month
  )

  @derive(decoder, encoder, show)
  case class UpdateHoliday(
    id: HolidayId,
    name: HolidayName,
    day: DayOfMonth,
    month: Month
  )

  @derive(decoder, encoder, show)
  case class UpdateTemplateInHoliday(
    id: HolidayId,
    smsWomenId: Option[TemplateId],
    smsMenId: Option[TemplateId]
  )
}
