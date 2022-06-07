package com.itforelead.smspaltfrom.domain

import cats.Show
import io.circe.{Decoder, Encoder}

sealed abstract class GenderAccess(val value: String)

object GenderAccess {
  case object ALL    extends GenderAccess("all")
  case object MALE   extends GenderAccess("male")
  case object FEMALE extends GenderAccess("female")

  val genderAccess = List(ALL, MALE, FEMALE)

  def find(value: String): Option[GenderAccess] =
    genderAccess.find(_.value == value.toLowerCase)

  def unsafeFrom(value: String): GenderAccess =
    find(value).getOrElse(throw new IllegalArgumentException(s"value doesn't match [ $value ]"))

  implicit val encStatus: Encoder[GenderAccess] = Encoder.encodeString.contramap[GenderAccess](_.value)
  implicit val decStatus: Decoder[GenderAccess] = Decoder.decodeString.map(unsafeFrom)
  implicit val show: Show[GenderAccess]         = Show.show(_.value)

}
