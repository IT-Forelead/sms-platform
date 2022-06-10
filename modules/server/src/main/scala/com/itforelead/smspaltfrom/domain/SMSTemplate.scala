package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.types._
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder, show)
case class SMSTemplate(
  id: TemplateId,
  templateCategoryId: TemplateCategoryId,
  title: Title,
  text: Content,
  genderAccess: GenderAccess
)

object SMSTemplate {
  @derive(decoder, encoder, show)
  case class CreateSMSTemplate(
    templateCategoryId: TemplateCategoryId,
    title: Title,
    text: Content,
    genderAccess: GenderAccess
  )

  @derive(decoder, encoder, show)
  case class SMSTemplateWithCatName(
    id: TemplateId,
    templateCategoryId: TemplateCategoryId,
    title: Title,
    text: Content,
    genderAccess: GenderAccess,
    categoryName: TemplateCategoryName
  )
}
