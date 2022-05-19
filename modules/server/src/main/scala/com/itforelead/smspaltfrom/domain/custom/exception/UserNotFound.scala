package com.itforelead.smspaltfrom.domain.custom.exception

import com.itforelead.smspaltfrom.domain.custom.refinements.EmailAddress

import scala.util.control.NoStackTrace

case class UserNotFound(email: EmailAddress) extends NoStackTrace
