package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.{Birthday, ContactId, CreatedAt, FirstName, LastName}
import com.itforelead.smspaltfrom.domain.{Contact, types}
import skunk._
import skunk.implicits._

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val columns: Codec[(((((ContactId, CreatedAt), FirstName), LastName), Birthday), Tel)] =
    contactId ~ createdAt ~ firstName ~ lastName ~ birthday ~ tel

  val encoder: Encoder[Contact] =
    columns.contramap(c => c.id ~ c.createdAt ~ c.firstName ~ c.lastName ~ c.birthday ~ c.phone)

  val decoder: Decoder[Contact] =
    columns.map { case id ~ createdAt ~ firstname ~ lastname ~ birthday ~ phone =>
      Contact(id, createdAt, firstname, lastname, birthday, phone)
    }

  val insert: Query[Contact, Contact] =
    sql"""INSERT INTO users VALUES ($encoder) returning *""".query(decoder)

}
