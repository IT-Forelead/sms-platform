package smsplatform

import ciris.Secret
import eu.timepit.refined.types.string.NonEmptyString
import com.itforelead.smspaltfrom.config.JwtConfig
import com.itforelead.smspaltfrom.types.{JwtAccessTokenKeyConfig, PasswordSalt, TokenExpiration}

import scala.concurrent.duration.DurationInt

package object config {

  val jwtConfig: JwtConfig =
    JwtConfig(
      Secret(JwtAccessTokenKeyConfig(NonEmptyString.unsafeFrom("dah3EeJ8xohtaeJ5ahyah-"))),
      Secret(PasswordSalt(NonEmptyString.unsafeFrom("06!grsnxXG0d*Pj496p6fuA*o"))),
      TokenExpiration(30.minutes)
    )
}
