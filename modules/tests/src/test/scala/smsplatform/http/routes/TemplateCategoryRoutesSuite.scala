package smsplatform.http.routes

import cats.effect.{IO, Sync}
import com.itforelead.smspaltfrom.Application.logger
import com.itforelead.smspaltfrom.domain.TemplateCategory
import com.itforelead.smspaltfrom.domain.TemplateCategory.CreateTemplateCategory
import com.itforelead.smspaltfrom.domain.types.TemplateCategoryId
import com.itforelead.smspaltfrom.routes.{TemplateCategoryRoutes, deriveEntityEncoder}
import com.itforelead.smspaltfrom.services.TemplateCategories
import org.http4s.Method.{DELETE, GET, POST, PUT}
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax

import smsplatform.stub_services.TemplateCategoriesStub
import smsplatform.utils.Generators.{
  createTemplateCategoryGen,
  templateCategoryGen,
  templateIdGen,
  userGen
}
import smsplatform.utils.HttpSuite

object TemplateCategoryRoutesSuite extends HttpSuite {

  def TemplateCategories[F[_]: Sync](from: TemplateCategory): TemplateCategories[F] = new TemplateCategoriesStub[F] {
    override def create(form: CreateTemplateCategory): F[TemplateCategory] = Sync[F].delay(from)

    override def templates: F[List[TemplateCategory]] = Sync[F].delay(List(from))

    override def update(form: TemplateCategory): F[TemplateCategory] = Sync[F].delay(from)

    override def delete(id: TemplateCategoryId): F[Unit] = Sync[F].unit
  }

  test("Create Template Category") {
    val gen = for {
      u  <- userGen
      t  <- templateCategoryGen
      cs <- createTemplateCategoryGen
    } yield (u, t, cs)

    forall(gen) { case (user, category, newCategory) =>
      for {
        token <- authToken(user)
        req    = POST(newCategory, uri"/template-category").putHeaders(token)
        routes = TemplateCategoryRoutes[IO](TemplateCategories(category)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(category, Status.Created)
      } yield res
    }
  }

  test("Get Template Category") {
    val gen = for {
      u <- userGen
      t <- templateCategoryGen
    } yield (u, t)

    forall(gen) { case (user, category) =>
      for {
        token <- authToken(user)
        req    = GET(uri"/template-category").putHeaders(token)
        routes = TemplateCategoryRoutes[IO](TemplateCategories(category)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

  test("Update Template Category") {
    val gen = for {
      u  <- userGen
      t  <- templateCategoryGen
      nt <- templateCategoryGen
    } yield (u, t, nt)

    forall(gen) { case (user, category, newCategory) =>
      for {
        token <- authToken(user)
        req    = PUT(newCategory, uri"/template-category").putHeaders(token)
        routes = TemplateCategoryRoutes[IO](TemplateCategories(category)).routes(usersMiddleware)
        res <- expectHttpBodyAndStatus(routes, req)(category, Status.Ok)
      } yield res
    }
  }

  test("delete sms template") {
    val gen = for {
      u  <- userGen
      t  <- templateCategoryGen
      ti <- templateIdGen
    } yield (u, t, ti)

    forall(gen) { case (user, category, categoryId) =>
      for {
        token <- authToken(user)
        req    = DELETE(categoryId, uri"/template-category").putHeaders(token)
        routes = TemplateCategoryRoutes[IO](TemplateCategories(category)).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.NoContent)
      } yield res
    }
  }

}
