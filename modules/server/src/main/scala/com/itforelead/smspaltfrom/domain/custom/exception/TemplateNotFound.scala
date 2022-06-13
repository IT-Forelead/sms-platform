package com.itforelead.smspaltfrom.domain.custom.exception

import com.itforelead.smspaltfrom.domain.types.TemplateId

import scala.util.control.NoStackTrace

case class TemplateNotFound(id: TemplateId) extends NoStackTrace {
  override def getMessage: String = id.value.toString
}
