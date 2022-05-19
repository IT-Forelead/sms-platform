package com.itforelead.smspaltfrom.domain

import cats.effect.Sync
import cats.implicits._
import com.itforelead.smspaltfrom.domain.custom.utils.MapConvert
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import com.itforelead.smspaltfrom.domain.custom.refinements.{EmailAddress, Password}
import com.itforelead.smspaltfrom.domain.custom.utils.MapConvert.ValidationResult
import io.circe.refined._
import eu.timepit.refined.cats._

@derive(decoder, encoder, show)
case class Credentials(email: EmailAddress, password: Password)
object Credentials {

  implicit def decodeMap[F[_]: Sync]: MapConvert[F, ValidationResult[Credentials]] =
    (values: Map[String, String]) =>
      (
        values
          .get("email")
          .map(EmailAddress.unsafeFrom(_).validNec)
          .getOrElse("Field [ email ] isn't defined".invalidNec),
        values
          .get("password")
          .map(Password.unsafeFrom(_).validNec)
          .getOrElse("Field [ password ] isn't defined".invalidNec)
      ).mapN(Credentials.apply).pure[F]
}
