package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.types.{TemplateCategoryId, TemplateCategoryName, UserId}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder, show)
case class TemplateCategory(
  id: TemplateCategoryId,
  userId: UserId,
  name: TemplateCategoryName
)

object TemplateCategory {
  @derive(decoder, encoder, show)
  case class CreateTemplateCategory(name: TemplateCategoryName)

  @derive(decoder, encoder, show)
  case class UpdateTemplateCategory(id: TemplateCategoryId, name: TemplateCategoryName)
}
