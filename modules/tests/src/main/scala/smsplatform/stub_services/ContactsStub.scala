package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.Contact
import com.itforelead.smspaltfrom.domain.Contact.CreateContact
import com.itforelead.smspaltfrom.services.Contacts

class ContactsStub[F[_]] extends Contacts[F] {
  override def create(userParam: CreateContact): F[Contact] = ???
}
