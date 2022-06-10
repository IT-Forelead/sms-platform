package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.TemplateCategory
import com.itforelead.smspaltfrom.domain.TemplateCategory.CreateTemplateCategory
import com.itforelead.smspaltfrom.domain.types.TemplateCategoryId
import com.itforelead.smspaltfrom.services.TemplateCategories

class TemplateCategoriesStub[F[_]] extends TemplateCategories[F] {
  override def create(param: CreateTemplateCategory): F[TemplateCategory] = ???
  override def templateCategories: F[List[TemplateCategory]] = ???
  override def update(param: TemplateCategory): F[TemplateCategory] = ???
  override def delete(id: TemplateCategoryId): F[Unit] = ???
}
