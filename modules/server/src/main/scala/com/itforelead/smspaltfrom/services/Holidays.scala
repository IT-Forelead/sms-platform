package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.{Holiday, ID}
import com.itforelead.smspaltfrom.domain.Holiday.{CreateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.domain.types.HolidayId
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk._

trait Holidays[F[_]] {

  /** Function for create holiday
    * @param form
    *   holiday's params
    * @return
    *   created holiday
    */
  def create(form: CreateHoliday): F[Holiday]

  /** Function for get all holidays
    * @return
    *   list of holidays
    */
  def holidays: F[List[Holiday]]

  /** Function for update holiday
    * @param holiday
    *   holiday
    * @return
    *   updated holiday
    */
  def update(holiday: Holiday): F[Holiday]

  /** Function for update holiday
    * @param holiday
    *   holiday
    * @return
    *   updated templateId of holiday
    */
  def updateTemplateInHoliday(holiday: UpdateTemplateInHoliday): F[Holiday]

  /** Function for delete holiday
    * @param id
    *   holiday's id
    * @return
    *   unit
    */
  def delete(id: HolidayId): F[Unit]
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
      def create(form: CreateHoliday): F[Holiday] = {
        for {
          id <- ID.make[F, HolidayId]
          holiday <- prepQueryUnique(
            insert,
            Holiday(id, form.name, form.day, form.month)
          )
        } yield holiday
      }

      /** Function for get all holidays
        * @return
        *   list of holidays
        */
      override def holidays: F[List[Holiday]] =
        prepQueryList(select, Void)

      /** Function for update holiday
        * @param holiday
        *   holiday
        * @return
        *   updated holiday
        */
      override def update(holiday: Holiday): F[Holiday] =
        prepQueryUnique(updateSql, holiday)

      /** Function for update holiday
        * @param holiday
        *   holiday
        * @return
        *   updated templateId of holiday
        */
      override def updateTemplateInHoliday(holiday: UpdateTemplateInHoliday): F[Holiday] =
        prepQueryUnique(updateTemplateInHolidaySql, holiday)

      /** Function for delete holiday
        * @param id
        *   holiday's id
        * @return
        *   unit
        */
      override def delete(id: HolidayId): F[Unit] =
        prepCmd(deleteSql, id)
    }
}
