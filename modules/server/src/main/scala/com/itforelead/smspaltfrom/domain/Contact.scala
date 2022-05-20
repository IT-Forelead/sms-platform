package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.{ContactId, ContactName}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._
import eu.timepit.refined.cats._

@derive(decoder, encoder, show)
case class Contact(id: ContactId, name: ContactName, phone: Tel)

object Contact {
  @derive(decoder, encoder, show)
  case class CreateContact(name: ContactName, phone: Tel)
}
