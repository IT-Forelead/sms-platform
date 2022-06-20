package com.itforelead.smspaltfrom.domain.custom.exception

import com.itforelead.smspaltfrom.domain.Gender

import scala.util.control.NoStackTrace

case class GenderIncorrect(gender: Gender) extends NoStackTrace {
  override def getMessage: String = s"The gender of the contact is entered incorrectly. Gender [ ${gender.value} ]"
}
