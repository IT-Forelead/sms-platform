package com.itforelead.smspaltfrom.config

import ciris.Secret
import com.itforelead.smspaltfrom.types.{JwtAccessTokenKeyConfig, PasswordSalt, TokenExpiration}

case class JwtConfig(
  tokenConfig: Secret[JwtAccessTokenKeyConfig],
  passwordSalt: Secret[PasswordSalt],
  tokenExpiration: TokenExpiration
)
