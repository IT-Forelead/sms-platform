package com.itforelead.smspaltfrom.domain

import com.itforelead.smspaltfrom.domain.custom.refinements.Tel
import com.itforelead.smspaltfrom.domain.types.{
  ContactId,
  Content,
  FirstName,
  HolidayId,
  LastName,
  TemplateCategoryId,
  TemplateId,
  Title
}
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

import java.time.LocalDate

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

}
