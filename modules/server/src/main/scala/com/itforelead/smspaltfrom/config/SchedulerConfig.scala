package com.itforelead.smspaltfrom.config
import java.time.LocalTime
import scala.concurrent.duration.FiniteDuration

case class SchedulerConfig(
  startTime: LocalTime,
  period: FiniteDuration
)
