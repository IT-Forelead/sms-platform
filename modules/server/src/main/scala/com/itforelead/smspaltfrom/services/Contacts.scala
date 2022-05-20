package com.itforelead.smspaltfrom.services

import cats.effect.{Resource, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.domain.{Contact, ID}
import com.itforelead.smspaltfrom.domain.Contact.CreateContact
import com.itforelead.smspaltfrom.domain.types.ContactId
import com.itforelead.smspaltfrom.effects.GenUUID
import skunk._

trait Contacts[F[_]] {
  def create(form: CreateContact): F[Contact]
}

object Contacts {
  def apply[F[_]: GenUUID: Sync](implicit
    session: Resource[F, Session[F]]
  ): Contacts[F] =
    new Contacts[F] with SkunkHelper[F] {

      import com.itforelead.smspaltfrom.services.sql.ContactsSql._

      def create(form: CreateContact): F[Contact] =
        ID.make[F, ContactId]
          .flatMap { id =>
            prepQueryUnique(insert, Contact(id, form.name, form.phone))
          }

    }
}
