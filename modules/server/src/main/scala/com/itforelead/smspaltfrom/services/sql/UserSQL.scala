package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.domain.User.CreateUser
import skunk._
import skunk.implicits._
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import com.itforelead.smspaltfrom.domain.custom.refinements.EmailAddress
import com.itforelead.smspaltfrom.domain.types._
import com.itforelead.smspaltfrom.domain.Role

object UserSQL {
  val userId: Codec[UserId] = identity[UserId]

  private val Columns = userId ~ userName ~ email ~ gender ~ passwordHash ~ role

  val encoder: Encoder[UserId ~ CreateUser ~ PasswordHash[SCrypt]] =
    Columns.contramap { case i ~ u ~ p =>
      i ~ u.name ~ u.email ~ u.gender ~ p ~ Role.USER
    }

  val decoder: Decoder[User ~ PasswordHash[SCrypt]] =
    Columns.map { case i ~ n ~ e ~ g ~ p ~ r =>
      User(i, n, e, g, r) ~ p
    }

  val selectUser: Query[EmailAddress, User ~ PasswordHash[SCrypt]] =
    sql"""SELECT * FROM users WHERE email = $email""".query(decoder)

  val insertUser: Query[UserId ~ CreateUser ~ PasswordHash[SCrypt], User ~ PasswordHash[SCrypt]] =
    sql"""INSERT INTO users VALUES ($encoder) returning *""".query(decoder)

}
