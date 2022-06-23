package com.itforelead.smspaltfrom
import ciris.ConfigDecoder
import com.itforelead.smspaltfrom.domain.AppEnv
import org.http4s.Uri

package object config {

  implicit val UriConfigDecoder: ConfigDecoder[String, Uri] =
    ConfigDecoder[String].mapOption("Uri") { uri =>
      Uri.fromString(uri).toOption
    }

  implicit val AppEnvConfigDecoder: ConfigDecoder[String, AppEnv] =
    ConfigDecoder[String].mapOption("AppEnv") { env =>
      AppEnv.find(env)
    }
}
