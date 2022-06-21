package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Contact.UpdateContact
import com.itforelead.smspaltfrom.domain.Contact
import com.itforelead.smspaltfrom.domain.types.ContactId
import skunk._
import skunk.codec.all.{bool, date, timestamp}
import skunk.implicits._

import java.time.LocalDate

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val Columns = contactId ~ timestamp ~ firstName ~ lastName ~ gender ~ date ~ tel ~ bool

  val encoder: Encoder[Contact] =
    Columns.contramap(c => c.id ~ c.createdAt ~ c.firstName ~ c.lastName ~ c.gender ~ c.birthday ~ c.phone ~ false)

  val decoder: Decoder[Contact] =
    Columns.map { case id ~ createdAt ~ firstname ~ lastname ~ gender ~ birthday ~ phone ~ _ =>
      Contact(id, createdAt, firstname, lastname, gender, birthday, phone)
    }

  val insert: Query[Contact, Contact] =
    sql"""INSERT INTO contacts VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[Void, Contact] =
    sql"""SELECT * FROM contacts WHERE deleted = false""".query(decoder)

  val updateSql: Query[UpdateContact, Contact] =
    sql"""UPDATE contacts
         SET first_name = $firstName,
         last_name = $lastName,
         gender = $gender,
         birthday = $date,
         phone = $tel
         WHERE id = $contactId RETURNING *"""
      .query(decoder)
      .contramap[UpdateContact](c => c.firstName ~ c.lastName ~ c.gender ~ c.birthday ~ c.phone ~ c.id)

  val deleteSql: Command[ContactId] =
    sql"""UPDATE contacts SET deleted = true WHERE id = $contactId""".command

  val selectByBirthday: Query[LocalDate, Contact] =
    sql"""SELECT * FROM contacts
        WHERE DATE_PART('day', birthday) = date_part('day', $date) AND
        DATE_PART('month', birthday) = date_part('month',  $date)""".query(decoder).contramap(d => d ~ d)

}
