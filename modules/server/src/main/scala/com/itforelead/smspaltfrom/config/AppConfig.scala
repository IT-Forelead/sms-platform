package com.itforelead.smspaltfrom.config

import com.itforelead.smspaltfrom.domain.AppEnv

case class AppConfig(
  env: AppEnv,
  jwtConfig: JwtConfig,
  dbConfig: DBConfig,
  redis: RedisConfig,
  serverConfig: HttpServerConfig,
  logConfig: LogConfig,
  messageBroker: BrokerConfig,
  scheduler: SchedulerConfig
)
