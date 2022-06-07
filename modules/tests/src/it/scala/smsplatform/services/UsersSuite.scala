package smsplatform.services

import cats.effect.IO
import com.itforelead.smspaltfrom.services.{Contacts, Users}
import eu.timepit.refined.auto.autoUnwrap
import smsplatform.services.ContactsSuite.{assert, test}
import smsplatform.utils.DBSuite
import smsplatform.utils.Generators.{createContactGen, createUserGen}
import tsec.passwordhashers.jca.SCrypt

object UsersSuite extends DBSuite {

  test("Create User") { implicit postgres =>
    val users = Users[IO]
    forall(createUserGen) { createUser =>
      SCrypt.hashpw[IO](createUser.password).flatMap { hash =>
        for {
          user1 <- users.create(createUser, hash)
          user2 <- users.find(user1.email)
        } yield assert(user2.exists(_.user == user1))
      }
    }
  }
}
