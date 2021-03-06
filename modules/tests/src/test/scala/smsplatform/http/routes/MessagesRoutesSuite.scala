package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.Message.MessageWithContact
import com.itforelead.smspaltfrom.domain.types.UserId
import com.itforelead.smspaltfrom.domain.{Contact, Message, SMSTemplate}
import com.itforelead.smspaltfrom.routes.MessageRoutes
import com.itforelead.smspaltfrom.services.Messages
import org.http4s.Method.GET
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.MessagesStub
import smsplatform.utils.Generators._
import smsplatform.utils.HttpSuite

object MessagesRoutesSuite extends HttpSuite {

  def messagesForGet[F[_]: Sync](message: Message, contact: Contact, template: SMSTemplate): Messages[F] =
    new MessagesStub[F] {
      override def messages(userId: UserId): F[List[MessageWithContact]] =
        Sync[F].delay(List(MessageWithContact(message, contact, template)))
    }

  test("get messages") {
    val gen = for {
      u <- userGen
      m <- messageGen
      c <- contactGen
      t <- smsTemplateGen
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
}
