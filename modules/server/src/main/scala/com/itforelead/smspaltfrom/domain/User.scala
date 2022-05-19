package com.itforelead.smspaltfrom.domain

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import com.itforelead.smspaltfrom.domain.custom.refinements.{EmailAddress, Password}
import types._
import io.circe.refined._
import eu.timepit.refined.cats._

@derive(decoder, encoder, show)
case class User(id: UserId, name: UserName, email: EmailAddress, gender: Gender, role: Role)
object User {

  @derive(decoder, encoder, show)
  case class CreateUser(
    name: UserName,
    email: EmailAddress,
    gender: Gender,
    password: Password
  )

  @derive(decoder, encoder)
  case class UserWithPassword(user: User, password: PasswordHash[SCrypt])
}
