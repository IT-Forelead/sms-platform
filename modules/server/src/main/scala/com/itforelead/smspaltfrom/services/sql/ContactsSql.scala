package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Contact.UpdateContact
import com.itforelead.smspaltfrom.domain.Contact
import com.itforelead.smspaltfrom.domain.types.{ContactId, UserId}
import com.itforelead.smspaltfrom.services.sql.UserSQL.userId
import skunk._
import skunk.codec.all.{bool, date, timestamp}
import skunk.implicits._

import java.time.LocalDate

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val Columns = contactId ~ userId ~ timestamp ~ firstName ~ lastName ~ gender ~ date ~ tel ~ bool

  val encoder: Encoder[Contact] =
    Columns.contramap(c =>
      c.id ~ c.userId ~ c.createdAt ~ c.firstName ~ c.lastName ~ c.gender ~ c.birthday ~ c.phone ~ false
    )

  val decoder: Decoder[Contact] =
    Columns.map { case id ~ userId ~ createdAt ~ firstname ~ lastname ~ gender ~ birthday ~ phone ~ _ =>
      Contact(id, userId, createdAt, firstname, lastname, gender, birthday, phone)
    }

  val insert: Query[Contact, Contact] =
    sql"""INSERT INTO contacts VALUES ($encoder) RETURNING *""".query(decoder)

  val select: Query[UserId, Contact] =
    sql"""SELECT * FROM contacts WHERE user_id = $userId AND deleted = false""".query(decoder)

  val updateSql: Query[UpdateContact ~ UserId, Contact] =
    sql"""UPDATE contacts
         SET first_name = $firstName,
         last_name = $lastName,
         gender = $gender,
         birthday = $date,
         phone = $tel
         WHERE id = $contactId AND user_id = $userId RETURNING *"""
      .query(decoder)
      .contramap[UpdateContact ~ UserId] { case (c, uId) =>
        c.firstName ~ c.lastName ~ c.gender ~ c.birthday ~ c.phone ~ c.id ~ uId
      }

  val deleteSql: Command[ContactId ~ UserId] =
    sql"""UPDATE contacts SET deleted = true WHERE id = $contactId AND user_id = $userId""".command

  val selectByBirthday: Query[LocalDate, Contact] =
    sql"""SELECT * FROM contacts
        WHERE DATE_PART('day', birthday) = date_part('day', $date) AND
        DATE_PART('month', birthday) = date_part('month',  $date)""".query(decoder).contramap(d => d ~ d)

}
