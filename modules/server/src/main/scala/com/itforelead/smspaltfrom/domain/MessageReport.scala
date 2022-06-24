package com.itforelead.smspaltfrom.domain
import com.itforelead.smspaltfrom.domain.types.TemplateCategoryName

import java.time.LocalDateTime

case class MessageReport(
  template: SMSTemplate,
  category: TemplateCategoryName,
  total: Int,
  delivered: Int,
  sentDate: LocalDateTime
)
