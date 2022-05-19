package com.itforelead.smspaltfrom.config

import scala.concurrent.duration.FiniteDuration

case class HttpClientConfig(
  timeout: FiniteDuration,
  idleTimeInPool: FiniteDuration
)
