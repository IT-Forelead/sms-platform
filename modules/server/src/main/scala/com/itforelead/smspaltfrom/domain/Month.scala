package com.itforelead.smspaltfrom.domain

import cats.Show
import io.circe.{Decoder, Encoder}

sealed abstract class Month(val value: String)

object Month {
  case object JANUARY   extends Month("january")
  case object FEBRUARY extends Month("february")
  case object MARCH extends Month("march")
  case object APRIL extends Month("april")
  case object MAY extends Month("may")
  case object JUNE extends Month("june")
  case object JULY extends Month("july")
  case object AUGUST extends Month("august")
  case object SEPTEMBER extends Month("september")
  case object OCTOBER extends Month("october")
  case object NOVEMBER extends Month("november")
  case object DECEMBER extends Month("december")

  val months = List(
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER
  )

  def find(value: String): Option[Month] =
    months.find(_.value == value.toLowerCase)

  def unsafeFrom(value: String): Month =
    find(value).getOrElse(throw new IllegalArgumentException(s"value doesn't match [ $value ]"))

  implicit val encStatus: Encoder[Month] = Encoder.encodeString.contramap[Month](_.value)
  implicit val decStatus: Decoder[Month] = Decoder.decodeString.map(unsafeFrom)
  implicit val show: Show[Month]         = Show.show(_.value)
}
