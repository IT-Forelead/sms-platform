package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.domain.{Holiday, TemplateCategory}
import com.itforelead.smspaltfrom.services.{Holidays, TemplateCategories}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createHolidayGen, createTemplateCategoryGen, holidayNameGen, templateCategoryNameGen}

object TemplateCategorySuite extends DBSuite {

  test("Create Template Category") { implicit postgres =>
    val templateCategories = TemplateCategories[IO]
    forall(createTemplateCategoryGen) { createTemplateCategory =>
      for {
        tmplcat1 <- templateCategories.create(createTemplateCategory)
        tmplcat2 <- templateCategories.templates
      } yield assert(tmplcat2.contains(tmplcat1))
    }
  }

  test("Update Template Category") { implicit postgres =>
    val templateCategories = TemplateCategories[IO]
    val gen = for {
      c <- createTemplateCategoryGen
      t <- templateCategoryNameGen
    } yield (c, t)
    forall(gen) { case (createTemplateCategory, name) =>
      for {
        tmplcat1 <- templateCategories.create(createTemplateCategory)
        tmplcat2 <- templateCategories.update(
          TemplateCategory(
            id = tmplcat1.id,
            name = name
          )
        )
      } yield assert.same(tmplcat2.name, name)
    }
  }

  test("Delete Template Category") { implicit postgres =>
    val templateCategories = TemplateCategories[IO]
    forall(createTemplateCategoryGen) { createTemplateCategory =>
      for {
        tmplcat1 <- templateCategories.create(createTemplateCategory)
        _        <- templateCategories.delete(tmplcat1.id)
        tmplcat3 <- templateCategories.templates
      } yield assert(!tmplcat3.contains(tmplcat1))
    }
  }

}
