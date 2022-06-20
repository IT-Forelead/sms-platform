package com.itforelead.smspaltfrom.services

import cats.data.NonEmptyList
import cats.effect.Async
import com.itforelead.smspaltfrom.config.BrokerConfig
import com.itforelead.smspaltfrom.domain.broker.{BrokerMessage, Content, SMS, SendSMS}
import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.MessageId
import eu.timepit.refined.auto._
import org.http4s.Method.POST
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import org.http4s.{AuthScheme, BasicCredentials, Credentials, MediaType, Request}
import org.typelevel.log4cats.Logger

trait MessageBroker[F[_]] {
  def send(messageId: MessageId, phone: Tel, text: String): F[Unit]
}

object MessageBroker {
  def apply[F[_]: Async: Logger](httpClint: Client[F], config: BrokerConfig): MessageBroker[F] =
    if (config.enabled)
      new MessageBrokerImpl[F](httpClint, config)
    else
      new MessageBrokerMock[F]

  private class MessageBrokerMock[F[_]: Logger] extends MessageBroker[F] {
    override def send(messageId: MessageId, phone: Tel, text: String): F[Unit] =
      Logger[F].info(
        s"""Congratulation message sent to [ $phone ],
              message text [
                $text
              ] """
      )
  }

  private class MessageBrokerImpl[F[_]: Async](httpClint: Client[F], config: BrokerConfig)
      extends MessageBroker[F]
      with Http4sClientDsl[F] {
    private val ORIGINATOR: String = "3700"

    private def makeRequest(sms: SendSMS): Request[F] =
      POST(
        sms,
        config.apiURL,
        Authorization(Credentials.Token(AuthScheme.Basic, BasicCredentials(config.login, config.password.value).token)),
        Accept(MediaType.application.json)
      )

    private def makeSMS(messageId: MessageId, phone: Tel, text: String): SendSMS =
      SendSMS(NonEmptyList.one(BrokerMessage(phone, messageId, text, SMS(ORIGINATOR, Content(text)))))

    override def send(messageId: MessageId, phone: Tel, text: String): F[Unit] =
      httpClint.expect(makeRequest(makeSMS(messageId, phone, text)))
  }

}
