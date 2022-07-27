package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.SMSTemplate
import com.itforelead.smspaltfrom.domain.SMSTemplate.{CreateSMSTemplate, SMSTemplateWithCatName, UpdateSMSTemplate}
import com.itforelead.smspaltfrom.domain.types.{TemplateId, UserId}
import com.itforelead.smspaltfrom.services.SMSTemplates

class SMSTemplatesStub[F[_]] extends SMSTemplates[F] {
  override def create(userId: UserId, param: CreateSMSTemplate): F[SMSTemplate]     = ???
  override def templates(userId: UserId): F[List[SMSTemplateWithCatName]]           = ???
  override def update(userId: UserId, param: UpdateSMSTemplate): F[SMSTemplate]           = ???
  override def delete(id: TemplateId, userId: UserId): F[Unit]                      = ???
  override def find(templateId: TemplateId): F[Option[SMSTemplate]]                 = ???
}
