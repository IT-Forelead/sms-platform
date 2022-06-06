package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.{Contact, Message, SMSTemplate}
import com.itforelead.smspaltfrom.domain.Message.{CreateMessage, MessageWithContact}
import com.itforelead.smspaltfrom.domain.types.ContactId
import com.itforelead.smspaltfrom.routes.{MessageRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.Messages
import org.http4s.Method.{GET, POST}
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.MessagesStub
import smsplatform.utils.Generators._
import smsplatform.utils.HttpSuite

object MessagesRoutesSuite extends HttpSuite {

  def messages[F[_]: Sync](message: Message): Messages[F] = new MessagesStub[F] {
    override def create(form: CreateMessage): F[Message] = Sync[F].delay(message)
  }

  def messagesForGet[F[_]: Sync](message: Message, contact: Contact, template: SMSTemplate): Messages[F] = new MessagesStub[F] {
    override def messages: F[List[MessageWithContact]] =
      Sync[F].delay(List(MessageWithContact(message, contact, template)))

    override def messagesByContactId(id: ContactId): F[List[MessageWithContact]] =
      Sync[F].delay(List(MessageWithContact(message, contact, template)))
  }

  test("create message") {
    val gen = for {
      u  <- userGen
      cm <- createMessageGen
      m  <- messageGen
    } yield (u, m, cm)

    forall(gen) { case (user, message, newMessage) =>
      for {
        token <- authToken(user)
        req    = POST(newMessage, uri"/messages").putHeaders(token)
        routes = MessageRoutes[IO](messages(message)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(message, Status.Created)
      } yield res
    }
  }

  test("get messages") {
    val gen = for {
      u  <- userGen
      m  <- messageGen
      c  <- contactGen
      t  <- smsTemplateGen
    } yield (u, m, c, t)

    forall(gen) { case (user, message, contact, smsTemplate) =>
      for {
        token <- authToken(user)
        req    = GET(uri"/messages").putHeaders(token)
        routes = MessageRoutes[IO](messagesForGet(message, contact, smsTemplate)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

  test("get messages by contactId") {
    val gen = for {
      u  <- userGen
      m  <- messageGen
      c  <- contactGen
      t  <- smsTemplateGen
      ci <- contactIdGen
    } yield (u, m, c, t, ci)

    forall(gen) { case (user, message, contact, smsTemplate, contactId) =>
      for {
        token <- authToken(user)
        req    = GET(Uri.unsafeFromString(s"/messages/${contactId.value}")).putHeaders(token)
        routes = MessageRoutes[IO](messagesForGet(message, contact, smsTemplate)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

}
