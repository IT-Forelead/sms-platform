package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.Message.{CreateMessage, MessageWithContact}
import com.itforelead.smspaltfrom.domain.types.UserId
import com.itforelead.smspaltfrom.domain.{DeliveryStatus, Message, MessageReport, types}
import com.itforelead.smspaltfrom.services.Messages

class MessagesStub[F[_]] extends Messages[F] {
  override def create(userId: UserId, userParam: CreateMessage): F[Message]          = ???
  override def messages(userId: UserId): F[List[MessageWithContact]]                 = ???
  override def changeStatus(id: types.MessageId, status: DeliveryStatus): F[Message] = ???
  override def getReport: F[List[MessageReport]]                                     = ???
}
