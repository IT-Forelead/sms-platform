package com.itforelead.smspaltfrom.domain.broker
import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.MessageId
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.refined._
import eu.timepit.refined.cats._

@derive(decoder, encoder, show)
case class BrokerMessage(
  recipient: Tel,
  messageId: MessageId,
  text: String,
  sms: SMS
)
