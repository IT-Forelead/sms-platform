package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.types.HolidayId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object HolidaysSql {
  val holidayId: Codec[HolidayId] = identity[HolidayId]

  private val Columns =
    holidayId ~ holidayName ~ dayOfMonth ~ month ~ SMSTemplateSql.templateId.opt ~ SMSTemplateSql.templateId.opt ~ SMSTemplateSql.templateId.opt ~ bool

  val encoder: Encoder[Holiday] =
    Columns.contramap(c => c.id ~ c.name ~ c.day ~ c.month ~ c.smsWomenId ~ c.smsMenId ~ c.smsAllId ~ false)

  val decoder: Decoder[Holiday] =
    Columns.map { case id ~ name ~ day ~ month ~ smsWomenId ~ smsMenId ~ smsAllId ~ _ =>
      Holiday(id, name, day, month, smsWomenId, smsMenId, smsAllId)
    }

  val insert: Query[Holiday, Holiday] =
    sql"""INSERT INTO holidays VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[Void, Holiday] =
    sql"""SELECT * FROM holidays WHERE deleted = false""".query(decoder)

  val updateSql: Query[Holiday, Holiday] =
    sql"""UPDATE holidays
         SET name = $holidayName,
         day = $dayOfMonth,
         month = $month,
         sms_women_id = $smsWomenId.opt,
         sms_men_id = $smsMenId.opt,
         sms_all_id = $smsAllId.opt
         WHERE id = $holidayId RETURNING *"""
      .query(decoder)
      .contramap[Holiday](h => h.name ~ h.day ~ h.month ~ h.smsWomenId ~ h.smsMenId ~ h.smsAllId ~ h.id)

  val deleteSql: Command[HolidayId] =
    sql"""UPDATE holidays SET deleted = true WHERE id = $holidayId""".command

}
