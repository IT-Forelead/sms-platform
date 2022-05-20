package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.ContactId
import com.itforelead.smspaltfrom.domain.{Contact, types}
import skunk._
import skunk.implicits._

object ContactsSql {
  val contactId: Codec[ContactId] = identity[ContactId]

  val columns: Codec[((ContactId, types.ContactName), Tel)] = contactId ~ contactName ~ tel

  val encoder: Encoder[Contact] =
    columns.contramap(c => c.id ~ c.name ~ c.phone)

  val decoder: Decoder[Contact] =
    columns.map { case id ~ name ~ phone =>
      Contact(id, name, phone)
    }

  val insert: Query[Contact, Contact] =
    sql"""INSERT INTO users VALUES ($encoder) returning *""".query(decoder)

}
