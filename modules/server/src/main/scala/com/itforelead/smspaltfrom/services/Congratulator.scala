package com.itforelead.smspaltfrom.services

import cats.Applicative
import cats.implicits.toFunctorOps

import java.time.LocalDate

trait Congratulator[F[_]] {
  def start: F[Unit]
}

object Congratulator {
  def make[F[_]: Applicative](contacts: Contacts[F]): Congratulator[F] = new Congratulator[F] {
    override def start: F[Unit] = contacts.findByBirthday(LocalDate.now()).void
  }
}
