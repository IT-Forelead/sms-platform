package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.{Holiday, ID}
import com.itforelead.smspaltfrom.domain.Holiday.{CreateHoliday, UpdateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.domain.types.{HolidayId, UserId}
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk._
import skunk.implicits.toIdOps

trait Holidays[F[_]] {

  /** Function for create holiday
    * @param form
    *   holiday's params
    * @return
    *   created holiday
    */
  def create(userId: UserId, form: CreateHoliday): F[Holiday]

  /** Function for get all holidays
    * @return
    *   list of holidays
    */
  def holidays(userId: UserId): F[List[Holiday]]

  /** Function for update holiday
    * @param holiday
    *   holiday
    * @return
    *   updated holiday
    */
  def update(userId: UserId, holiday: UpdateHoliday): F[Holiday]

  /** Function for update holiday
    * @param holiday
    *   holiday
    * @return
    *   updated templateId of holiday
    */
  def updateTemplateInHoliday(userId: UserId, holiday: UpdateTemplateInHoliday): F[Holiday]

  /** Function for delete holiday
    * @param id
    *   holiday's id
    * @return
    *   unit
    */
  def delete(id: HolidayId, userId: UserId): F[Unit]

  /** @return
    *   Today's holidays
    */
  def holidaysOfToday(userId: UserId): F[List[Holiday]]
}

object Holidays {

  /** @param session
    *   skunk session for connection postgres
    * @tparam F
    *   effect type
    * @return
    *   [[Holidays]]
    */
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): Holidays[F] =
    new Holidays[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.HolidaysSql._

      /** Function for create holiday
        * @param form
        *   holiday's params
        * @return
        *   created holiday
        */
      def create(userId: UserId, form: CreateHoliday): F[Holiday] = {
        for {
          id <- ID.make[F, HolidayId]
          holiday <- prepQueryUnique(
            insert,
            Holiday(id, userId, form.name, form.day, form.month)
          )
        } yield holiday
      }

      /** Function for get all holidays
        * @return
        *   list of holidays
        */
      override def holidays(userId: UserId): F[List[Holiday]] =
        prepQueryList(select, userId)

      /** Function for update holiday
        * @param holiday
        *   holiday
        * @return
        *   updated holiday
        */
      override def update(userId: UserId, holiday: UpdateHoliday): F[Holiday] =
        prepQueryUnique(updateSql, holiday ~ userId)

      /** Function for update holiday
        * @param holiday
        *   holiday
        * @return
        *   updated templateId of holiday
        */
      override def updateTemplateInHoliday(userId: UserId, holiday: UpdateTemplateInHoliday): F[Holiday] =
        prepQueryUnique(updateTemplateInHolidaySql, holiday ~ userId)

      /** Function for delete holiday
        * @param id
        *   holiday's id
        * @return
        *   unit
        */
      override def delete(id: HolidayId, userId: UserId): F[Unit] =
        prepCmd(deleteSql, id ~ userId)

      override def holidaysOfToday(userId: UserId): F[List[Holiday]] =
        prepQueryList(selectHolidaysOfToday, userId)
    }
}
