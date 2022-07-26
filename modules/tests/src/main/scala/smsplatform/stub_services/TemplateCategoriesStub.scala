package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.TemplateCategory
import com.itforelead.smspaltfrom.domain.TemplateCategory.{CreateTemplateCategory, UpdateTemplateCategory}
import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, UserId}
import com.itforelead.smspaltfrom.services.TemplateCategories

class TemplateCategoriesStub[F[_]] extends TemplateCategories[F] {
  override def create(userId: UserId, param: CreateTemplateCategory): F[TemplateCategory] = ???
  override def templateCategories(userId: UserId): F[List[TemplateCategory]] = ???
  override def update(userId: UserId, param: UpdateTemplateCategory): F[TemplateCategory] = ???
  override def delete(id: TemplateCategoryId, userId: UserId): F[Unit] = ???
}
