package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.Contact.{CreateContact, UpdateContact}
import com.itforelead.smspaltfrom.domain.types.ContactId
import com.itforelead.smspaltfrom.domain.{Contact, ID}
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk._

import java.time.{LocalDate, LocalDateTime}

trait Contacts[F[_]] {
  def create(form: CreateContact): F[Contact]
  def contacts: F[List[Contact]]
  def update(contact: UpdateContact): F[Contact]
  def delete(id: ContactId): F[Unit]

  /** Function for find contacts born on this day for congrats
    * @param birthday
    *   contact's birthday
    * @return
    *   list of contacts
    */
  def findByBirthday(birthday: LocalDate): F[List[Contact]]
}

object Contacts {

  /** @param session
    *   skunk session for connection postgres
    * @tparam F
    *   effect type
    * @return
    *   [[Contacts]]
    */
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): Contacts[F] =
    new Contacts[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.ContactsSql._

      def create(form: CreateContact): F[Contact] = {
        for {
          id  <- ID.make[F, ContactId]
          now <- Sync[F].delay(LocalDateTime.now())
          contact <- prepQueryUnique(
            insert,
            Contact(id, now, form.firstName, form.lastName, form.gender, form.birthday, form.phone)
          )
        } yield contact
      }

      override def contacts: F[List[Contact]] =
        prepQueryList(select, Void)

      override def update(contact: UpdateContact): F[Contact] =
        prepQueryUnique(updateSql, contact)

      override def delete(id: ContactId): F[Unit] =
        prepCmd(deleteSql, id)

      /** Function for find contacts born on this day for congrats
        * @param birthday
        *   contact's birthday
        * @return
        *   list of contacts
        */
      override def findByBirthday(birthday: LocalDate): F[List[Contact]] =
        prepQueryList(selectByBirthday, birthday)
    }
}
