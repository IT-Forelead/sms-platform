package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.{ContactId, FirstName, LastName}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._
import eu.timepit.refined.cats._

import java.time.LocalDateTime

@derive(decoder, encoder, show)
case class Contact(
  id: ContactId,
  createdAt: LocalDateTime,
  firstName: FirstName,
  lastName: LastName,
  birthday: LocalDateTime,
  phone: Tel
)

object Contact {
  @derive(decoder, encoder, show)
  case class CreateContact(firstName: FirstName, lastName: LastName, birthday: LocalDateTime, phone: Tel)
}
