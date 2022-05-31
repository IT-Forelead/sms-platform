package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.Contact.UpdateContact
import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.{ContactId, FirstName, LastName}
import com.itforelead.smspaltfrom.domain.{Contact, types}
import skunk._
import skunk.codec.all.timestamp
import skunk.implicits._

import java.time.LocalDateTime

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val columns: Codec[(((((ContactId, LocalDateTime), FirstName), LastName), LocalDateTime), Tel)] =
    contactId ~ timestamp ~ firstName ~ lastName ~ timestamp ~ tel

  val encoder: Encoder[Contact] =
    columns.contramap(c => c.id ~ c.createdAt ~ c.firstName ~ c.lastName ~ c.birthday ~ c.phone)

  val decoder: Decoder[Contact] =
    columns.map { case id ~ createdAt ~ firstname ~ lastname ~ birthday ~ phone =>
      Contact(id, createdAt, firstname, lastname, birthday, phone)
    }

  val insert: Query[Contact, Contact] =
    sql"""INSERT INTO contacts VALUES ($encoder) returning *""".query(decoder)

  val select: Query[Void, Contact] =
    sql"""SELECT * FROM contacts""".query(decoder)

  val updateSql: Query[UpdateContact, Contact] =
    sql"""UPDATE contacts
         SET first_name = $firstName,
         last_name = $lastName,
         birthday = $timestamp,
         phone = $tel
         WHERE id = $contactId RETURNING *"""
      .query(decoder)
      .contramap[UpdateContact](c => c.firstName ~ c.lastName ~ c.birthday ~ c.phone ~ c.id)

  val deleteSql: Command[ContactId] =
    sql"""DELETE FROM contacts WHERE id = $contactId""".command

}
