package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.domain.TemplateCategory.UpdateTemplateCategory
import com.itforelead.smspaltfrom.domain.{Holiday, TemplateCategory}
import com.itforelead.smspaltfrom.services.{Holidays, TemplateCategories}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createHolidayGen, createTemplateCategoryGen, defaultUserId, holidayNameGen, templateCategoryNameGen}

object TemplateCategorySuite extends DBSuite {

  test("Create Template Category") { implicit postgres =>
    val templateCategories = TemplateCategories[IO]
    forall(createTemplateCategoryGen) { createTemplateCategory =>
      for {
        tmplcat1 <- templateCategories.create(defaultUserId, createTemplateCategory)
        tmplcat2 <- templateCategories.templateCategories(defaultUserId)
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
        tmplcat1 <- templateCategories.create(defaultUserId, createTemplateCategory)
        tmplcat2 <- templateCategories.update(defaultUserId,
          UpdateTemplateCategory(
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
        tmplcat1 <- templateCategories.create(defaultUserId, createTemplateCategory)
        _        <- templateCategories.delete(tmplcat1.id, defaultUserId)
        tmplcat3 <- templateCategories.templateCategories(defaultUserId)
      } yield assert(!tmplcat3.contains(tmplcat1))
    }
  }

}
