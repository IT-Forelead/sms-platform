package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.SMSTemplate.{CreateSMSTemplate, SMSTemplateWithCatName}
import com.itforelead.smspaltfrom.domain.types.TemplateId
import com.itforelead.smspaltfrom.services.SMSTemplates

class SMSTemplatesStub[F[_]] extends SMSTemplates[F] {
  override def create(param: CreateSMSTemplate): F[SMSTemplate]     = ???
  override def templates: F[List[SMSTemplateWithCatName]]           = ???
  override def update(param: SMSTemplate): F[SMSTemplate]           = ???
  override def delete(id: TemplateId): F[Unit]                      = ???
  override def find(templateId: TemplateId): F[Option[SMSTemplate]] = ???
  override def activate(id: TemplateId): F[Unit]                    = ???
}
