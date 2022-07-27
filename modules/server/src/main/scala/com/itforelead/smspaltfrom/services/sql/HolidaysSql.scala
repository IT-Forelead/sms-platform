package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.Holiday.{UpdateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.domain.types.{HolidayId, UserId}
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import com.itforelead.smspaltfrom.services.sql.UserSQL.userId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object HolidaysSql {
  val holidayId: Codec[HolidayId] = identity[HolidayId]

  private val Columns =
    holidayId ~ userId ~ holidayName ~ dayOfMonth ~ month ~ templateId.opt ~ templateId.opt ~ bool

  val encoder: Encoder[Holiday] =
    Columns.contramap(c => c.id ~ c.userId ~ c.name ~ c.day ~ c.month ~ c.smsWomenId ~ c.smsMenId ~ false)

  val decoder: Decoder[Holiday] =
    Columns.map { case id ~ userId ~ name ~ day ~ month ~ smsWomenId ~ smsMenId ~ _ =>
      Holiday(id, userId, name, day, month, smsWomenId, smsMenId)
    }

  val insert: Query[Holiday, Holiday] =
    sql"""INSERT INTO holidays VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[UserId, Holiday] =
    sql"""SELECT * FROM holidays WHERE user_id = $userId AND deleted = false""".query(decoder)

  val updateTemplateInHolidaySql: Query[UpdateTemplateInHoliday ~ UserId, Holiday] =
    sql"""UPDATE holidays
         SET sms_women_id = ${templateId.opt},
         sms_men_id = ${templateId.opt}
         WHERE id = $holidayId AND user_id = $userId RETURNING *"""
      .query(decoder)
      .contramap[UpdateTemplateInHoliday ~ UserId] { case (h ~ uid) => h.smsWomenId ~ h.smsMenId ~ h.id ~ uid }

  val updateSql: Query[UpdateHoliday ~ UserId, Holiday] =
    sql"""UPDATE holidays
         SET name = $holidayName,
         day = $dayOfMonth,
         month = $month
         WHERE id = $holidayId AND user_id = $userId RETURNING *"""
      .query(decoder)
      .contramap[UpdateHoliday ~ UserId] { case (h ~ ui) => h.name ~ h.day ~ h.month ~ h.id ~ ui }

  val deleteSql: Command[HolidayId ~ UserId] =
    sql"""UPDATE holidays SET deleted = true WHERE id = $holidayId AND user_id = $userId""".command

  val selectHolidaysOfToday: Query[UserId, Holiday] =
    sql"""SELECT * FROM holidays
          WHERE DATE_PART('day', CURRENT_DATE) = day AND
          TO_CHAR(CURRENT_DATE, 'month') = month::text AND user_id = $userId AND deleted = false""".query(decoder)

}
