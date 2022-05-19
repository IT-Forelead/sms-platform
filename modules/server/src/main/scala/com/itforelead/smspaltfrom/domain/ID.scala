package com.itforelead.smspaltfrom.domain

import cats.Functor
import cats.implicits.toFunctorOps
import com.itforelead.smspaltfrom.effects.GenUUID
import com.itforelead.smspaltfrom.types.IsUUID

object ID {
  def make[F[_]: Functor: GenUUID, A: IsUUID]: F[A] =
    GenUUID[F].make.map(IsUUID[A]._UUID.get)

  def read[F[_]: Functor: GenUUID, A: IsUUID](str: String): F[A] =
    GenUUID[F].read(str).map(IsUUID[A]._UUID.get)
}
