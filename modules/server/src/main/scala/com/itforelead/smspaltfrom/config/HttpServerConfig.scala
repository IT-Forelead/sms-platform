package com.itforelead.smspaltfrom.config

import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.string.NonEmptyString

case class HttpServerConfig(
  host: NonEmptyString,
  port: UserPortNumber
)
