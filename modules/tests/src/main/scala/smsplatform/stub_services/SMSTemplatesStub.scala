package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.SMSTemplate.CreateSMSTemplate
import com.itforelead.smspaltfrom.services.SMSTemplates

class SMSTemplatesStub[F[_]] extends SMSTemplates[F] {
  override def create(param: CreateSMSTemplate): F[SMSTemplate] = ???
  override def templates: F[List[SMSTemplate]] = ???
}
