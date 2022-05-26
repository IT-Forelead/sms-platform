package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Contact
import com.itforelead.smspaltfrom.domain.types.ContactId
import skunk._
import skunk.codec.all.{date, timestamp}
import skunk.implicits._

import java.time.LocalDate

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val Columns = contactId ~ timestamp ~ firstName ~ lastName ~ date ~ tel

  val encoder: Encoder[Contact] =
    Columns.contramap(c => c.id ~ c.createdAt ~ c.firstName ~ c.lastName ~ c.birthday ~ c.phone)

  val decoder: Decoder[Contact] =
    Columns.map { case id ~ createdAt ~ firstname ~ lastname ~ birthday ~ phone =>
      Contact(id, createdAt, firstname, lastname, birthday, phone)
    }

  val insert: Query[Contact, Contact] =
    sql"""INSERT INTO contacts VALUES ($encoder) returning *""".query(decoder)

  val select: Query[Void, Contact] =
    sql"""SELECT * FROM contacts""".query(decoder)

  val selectByBirthday: Query[LocalDate, Contact] =
    sql"""SELECT * FROM contacts WHERE birthday = $date""".query(decoder)

}
