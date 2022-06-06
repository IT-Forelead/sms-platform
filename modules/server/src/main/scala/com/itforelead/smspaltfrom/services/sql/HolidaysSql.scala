package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.types.HolidayId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object HolidaysSql {
  val holidayId: Codec[HolidayId] = identity[HolidayId]

  val Columns = holidayId ~ holidayName ~ dayOfMonth ~ month ~ bool

  val encoder: Encoder[Holiday] =
    Columns.contramap(c => c.id ~ c.name ~ c.day ~ c.month ~ false)

  val decoder: Decoder[Holiday] =
    Columns.map { case id ~ name ~ day ~ month ~ _ =>
      Holiday(id, name, day, month)
    }

  val insert: Query[Holiday, Holiday] =
    sql"""INSERT INTO holidays VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[Void, Holiday] =
    sql"""SELECT * FROM holidays WHERE deleted = false""".query(decoder)

  val updateSql: Query[Holiday, Holiday] =
    sql"""UPDATE holidays
         SET name = $holidayName,
         day = $dayOfMonth,
         month = $month
         WHERE id = $holidayId RETURNING *"""
      .query(decoder)
      .contramap[Holiday](h => h.name ~ h.day ~ h.month ~ h.id)

  val deleteSql: Command[HolidayId] =
    sql"""UPDATE holidays SET deleted = true WHERE id = $holidayId""".command

}
