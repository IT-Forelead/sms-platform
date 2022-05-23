package smsplatform.http.routes

import cats.effect.{IO, Sync}
import cats.implicits._
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.Contact.CreateContact
import com.itforelead.smspaltfrom.domain.types.{ContactId, CreatedAt}
import com.itforelead.smspaltfrom.domain.{Contact, ID}
import com.itforelead.smspaltfrom.routes.{ContactRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.Contacts
import org.http4s.Method.POST
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.log4cats.Logger
import smsplatform.stub_services.ContactsStub
import smsplatform.utils.Generators.{booleanGen, contactGen, createContactGen, userGen}
import smsplatform.utils.HttpSuite

import java.time.LocalDateTime

object ContactRoutesSuite extends HttpSuite {

  def contacts[F[_]: Sync](contact: Contact): Contacts[F] = new ContactsStub[F] {
    override def create(
      form: CreateContact
    ): F[Contact] = Sync[F].delay(contact)
  }

  test("create") {
    val gen = for {
      u  <- userGen
      cc <- createContactGen
      c <- contactGen
    } yield (u, c, cc)

    forall(gen) { case (user, contact, newContact) =>
      for {
        token <- authToken(user)
        req          = POST(newContact, uri"/contact").putHeaders(token)
        routes       = ContactRoutes[IO](contacts(contact)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(contact, Status.Created)
      } yield res
    }
  }

}
