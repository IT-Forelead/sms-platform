package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.Contact
import com.itforelead.smspaltfrom.domain.Contact.CreateContact
import com.itforelead.smspaltfrom.routes.{ContactRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.Contacts
import org.http4s.Method.{GET, POST}
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import smsplatform.stub_services.ContactsStub
import smsplatform.utils.Generators.{contactGen, createContactGen, userGen}
import smsplatform.utils.HttpSuite

object ContactRoutesSuite extends HttpSuite {

  def contacts[F[_]: Sync](contact: Contact): Contacts[F] = new ContactsStub[F] {
    override def create(
      form: CreateContact
    ): F[Contact] = Sync[F].delay(contact)

    override def contacts: F[List[Contact]] =
      Sync[F].delay(List(contact))
  }

  test("create contact") {
    val gen = for {
      u  <- userGen
      cc <- createContactGen
      c  <- contactGen
    } yield (u, c, cc)

    forall(gen) { case (user, contact, newContact) =>
      for {
        token <- authToken(user)
        req    = POST(newContact, uri"/contact").putHeaders(token)
        routes = ContactRoutes[IO](contacts(contact)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(contact, Status.Created)
      } yield res
    }
  }

  test("get contacts") {
    val gen = for {
      u  <- userGen
      c  <- contactGen
    } yield (u, c)

    forall(gen) { case (user, contact) =>
      for {
        token <- authToken(user)
        req    = GET(uri"/contact").putHeaders(token)
        routes = ContactRoutes[IO](contacts(contact)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

}
