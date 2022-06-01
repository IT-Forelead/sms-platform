package com.itforelead.smspaltfrom.domain

import cats.Show
import io.circe.{Decoder, Encoder}

sealed abstract class Who(val value: String)

object Who {
  case object ALL   extends Who("all")
  case object MALE   extends Who("male")
  case object FEMALE extends Who("female")

  val who = List(ALL, MALE, FEMALE)

  def find(value: String): Option[Who] =
    who.find(_.value == value.toLowerCase)

  def unsafeFrom(value: String): Who =
    find(value).getOrElse(throw new IllegalArgumentException(s"value doesn't match [ $value ]"))

  implicit val encStatus: Encoder[Who] = Encoder.encodeString.contramap[Who](_.value)
  implicit val decStatus: Decoder[Who] = Decoder.decodeString.map(unsafeFrom)
  implicit val show: Show[Who]         = Show.show(_.value)

}


