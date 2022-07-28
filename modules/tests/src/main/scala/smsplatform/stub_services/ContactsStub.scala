package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.Contact
import com.itforelead.smspaltfrom.domain.Contact.{CreateContact, UpdateContact}
import com.itforelead.smspaltfrom.domain.types.{ContactId, UserId}
import com.itforelead.smspaltfrom.services.Contacts

import java.time.LocalDate

class ContactsStub[F[_]] extends Contacts[F] {
  override def create(userId: UserId, userParam: CreateContact): F[Contact]          = ???
  override def contacts(userId: UserId): F[List[Contact]]                            = ???
  override def update(userId: UserId, contact: UpdateContact): F[Contact]            = ???
  override def delete(id: ContactId, userId: UserId): F[Unit]                        = ???
  override def findByBirthday(birthday: LocalDate): F[List[Contact]]                 = ???
}
