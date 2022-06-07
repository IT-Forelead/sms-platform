package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.domain.Contact.UpdateContact
import com.itforelead.smspaltfrom.services.Contacts
import eu.timepit.refined.cats.refTypeShow
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createContactGen, phoneGen}

object ContactsSuite extends DBSuite {

  test("Create Contact") { implicit postgres =>
    val contacts = Contacts[IO]
    forall(createContactGen) { createContact =>
      for {
        contact1 <- contacts.create(createContact)
        contact2 <- contacts.contacts
      } yield assert(contact2.contains(contact1))
    }
  }

  test("Update Contact") { implicit postgres =>
    val contacts = Contacts[IO]
    val gen = for {
      t <- phoneGen
      c <- createContactGen
    } yield (c, t)
    forall(gen) { case (createContact, tel) =>
      for {
        contact1 <- contacts.create(createContact)
        contact2 <- contacts.update(
          UpdateContact(
            id = contact1.id,
            firstName = contact1.firstName,
            lastName = contact1.lastName,
            gender = contact1.gender,
            birthday = contact1.birthday,
            phone = tel
          )
        )
      } yield assert.same(contact2.phone, tel)
    }
  }

  test("Delete Contact") { implicit postgres =>
    val contacts = Contacts[IO]
    forall(createContactGen) { createContact =>
      for {
        contact1 <- contacts.create(createContact)
        _ <- contacts.delete(contact1.id)
        contact3 <- contacts.contacts
      } yield assert(!contact3.contains(contact1))
    }
  }

  test("Find Contact by birthday") { implicit postgres =>
    val contacts = Contacts[IO]
    forall(createContactGen) { createContact =>
      for {
        contact1 <- contacts.create(createContact)
        contact2 <- contacts.findByBirthday(contact1.birthday)
      } yield assert(contact2.nonEmpty)
    }
  }

}
