package com.itforelead.smspaltfrom.services

import eu.timepit.refined.types.string.NonEmptyString
import skunk.Codec
import skunk.codec.all._
import skunk.data.{Arr, Type}
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import com.itforelead.smspaltfrom.domain.custom.refinements.{DayOfMonth, EmailAddress, Tel}
import com.itforelead.smspaltfrom.domain.types.{
  Content,
  FirstName,
  HolidayName,
  LastName,
  TemplateCategoryName,
  Title,
  UserName
}
import com.itforelead.smspaltfrom.domain.{DeliveryStatus, Gender, GenderAccess, Month, Role}
import com.itforelead.smspaltfrom.types.IsUUID
import eu.timepit.refined.auto.autoUnwrap

import java.util.UUID
import scala.util.Try

package object sql {

  def parseUUID: String => Either[String, UUID] = s =>
    Try(Right(UUID.fromString(s))).getOrElse(Left(s"Invalid argument: [ $s ]"))

  val _uuid: Codec[Arr[UUID]] = Codec.array(_.toString, parseUUID, Type._uuid)

  val listUUID: Codec[List[UUID]] = _uuid.imap(_.flattenTo(List))(l => Arr(l: _*))

  def identity[A: IsUUID]: Codec[A] = uuid.imap[A](IsUUID[A]._UUID.get)(IsUUID[A]._UUID.apply)

  val userName: Codec[UserName] = varchar.imap[UserName](name => UserName(NonEmptyString.unsafeFrom(name)))(_.value)

  val firstName: Codec[FirstName] =
    varchar.imap[FirstName](firstname => FirstName(NonEmptyString.unsafeFrom(firstname)))(_.value)

  val templateCategoryName: Codec[TemplateCategoryName] =
    varchar.imap[TemplateCategoryName](templateCategoryName =>
      TemplateCategoryName(NonEmptyString.unsafeFrom(templateCategoryName))
    )(_.value)

  val lastName: Codec[LastName] =
    varchar.imap[LastName](lastname => LastName(NonEmptyString.unsafeFrom(lastname)))(_.value)

  val holidayName: Codec[HolidayName] =
    varchar.imap[HolidayName](name => HolidayName(NonEmptyString.unsafeFrom(name)))(_.value)

  val passwordHash: Codec[PasswordHash[SCrypt]] = varchar.imap[PasswordHash[SCrypt]](PasswordHash[SCrypt])(_.toString)

  val email: Codec[EmailAddress] = varchar.imap[EmailAddress](EmailAddress.unsafeFrom)(_.value)

  val tel: Codec[Tel] = varchar.imap[Tel](Tel.unsafeFrom)(_.value)

  val dayOfMonth: Codec[DayOfMonth] = int4.imap[DayOfMonth](DayOfMonth.unsafeFrom)(_.value)

  val gender: Codec[Gender] = `enum`[Gender](_.value, Gender.find, Type("gender"))

  val genderAccess: Codec[GenderAccess] = `enum`[GenderAccess](_.value, GenderAccess.find, Type("gender_access"))

  val role: Codec[Role] = `enum`[Role](_.value, Role.find, Type("role"))

  val month: Codec[Month] = `enum`[Month](_.value, Month.find, Type("month"))

  val deliveryStatus: Codec[DeliveryStatus] =
    `enum`[DeliveryStatus](_.value, DeliveryStatus.find, Type("delivery_status"))

  val content: Codec[Content] = varchar.imap[Content](content => Content(NonEmptyString.unsafeFrom(content)))(_.value)

  val title: Codec[Title] = varchar.imap[Title](title => Title(NonEmptyString.unsafeFrom(title)))(_.value)

}
