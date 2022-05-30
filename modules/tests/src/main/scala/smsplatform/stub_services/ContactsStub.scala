package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.{Contact, types}
import com.itforelead.smspaltfrom.domain.Contact.{CreateContact, UpdateContact}
import com.itforelead.smspaltfrom.domain.types.ContactId
import com.itforelead.smspaltfrom.services.Contacts

class ContactsStub[F[_]] extends Contacts[F] {
  override def create(userParam: CreateContact): F[Contact] = ???
  override def contacts: F[List[Contact]] = ???
  override def update(contact: UpdateContact): F[Contact] = ???
  override def delete(id: ContactId): F[Unit] = ???
}
