package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.Holiday.{UpdateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.domain.types.HolidayId
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object HolidaysSql {
  val holidayId: Codec[HolidayId] = identity[HolidayId]

  private val Columns =
    holidayId ~ holidayName ~ dayOfMonth ~ month ~ templateId.opt ~ templateId.opt ~ bool

  val encoder: Encoder[Holiday] =
    Columns.contramap(c => c.id ~ c.name ~ c.day ~ c.month ~ c.smsWomenId ~ c.smsMenId ~ false)

  val decoder: Decoder[Holiday] =
    Columns.map { case id ~ name ~ day ~ month ~ smsWomenId ~ smsMenId ~ _ =>
      Holiday(id, name, day, month, smsWomenId, smsMenId)
    }

  val insert: Query[Holiday, Holiday] =
    sql"""INSERT INTO holidays VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[Void, Holiday] =
    sql"""SELECT * FROM holidays WHERE deleted = false""".query(decoder)

  val updateTemplateInHolidaySql: Query[UpdateTemplateInHoliday, Holiday] =
    sql"""UPDATE holidays
         SET sms_women_id = ${templateId.opt},
         sms_men_id = ${templateId.opt}
         WHERE id = $holidayId RETURNING *"""
      .query(decoder)
      .contramap[UpdateTemplateInHoliday](h => h.smsWomenId ~ h.smsMenId ~ h.id)

  val updateSql: Query[UpdateHoliday, Holiday] =
    sql"""UPDATE holidays
         SET name = $holidayName,
         day = $dayOfMonth,
         month = $month
         WHERE id = $holidayId RETURNING *"""
      .query(decoder)
      .contramap[UpdateHoliday](h => h.name ~ h.day ~ h.month ~ h.id)

  val deleteSql: Command[HolidayId] =
    sql"""UPDATE holidays SET deleted = true WHERE id = $holidayId""".command

  val selectHolidaysOfToday: Query[Void, Holiday] =
    sql"""SELECT * FROM holidays
          WHERE DATE_PART('day', CURRENT_DATE) = day AND
          TO_CHAR(CURRENT_DATE, 'month') = month::text AND deleted = false""".query(decoder)

}
