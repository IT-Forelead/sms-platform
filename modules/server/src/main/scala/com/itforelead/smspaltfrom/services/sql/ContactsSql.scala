package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Contact.UpdateContact
import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.{ContactId, FirstName, LastName}
import com.itforelead.smspaltfrom.domain.{Contact, Gender}
import skunk._
import skunk.codec.all.{bool, timestamp}
import skunk.implicits._

import java.time.LocalDateTime

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val columns: Codec[(((((((ContactId, LocalDateTime), FirstName), LastName), Gender), LocalDateTime), Tel), Boolean)] =
    contactId ~ timestamp ~ firstName ~ lastName ~ gender ~ timestamp ~ tel ~ bool

  val encoder: Encoder[Contact] =
    columns.contramap(c => c.id ~ c.createdAt ~ c.firstName ~ c.lastName ~ c.gender ~ c.birthday ~ c.phone ~ false)

  val decoder: Decoder[Contact] =
    columns.map { case id ~ createdAt ~ firstname ~ lastname ~ gender ~ birthday ~ phone ~ _ =>
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
         birthday = $timestamp,
         phone = $tel
         WHERE id = $contactId RETURNING *"""
      .query(decoder)
      .contramap[UpdateContact](c => c.firstName ~ c.lastName ~ c.gender ~ c.birthday ~ c.phone ~ c.id)

  val deleteSql: Command[ContactId] =
    sql"""UPDATE contacts SET deleted = true WHERE id = $contactId""".command

}
