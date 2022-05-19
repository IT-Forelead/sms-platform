package smsplatform.stub_services

import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import com.itforelead.smspaltfrom.domain.User
import com.itforelead.smspaltfrom.domain.User.{CreateUser, UserWithPassword}
import com.itforelead.smspaltfrom.domain.custom.refinements.EmailAddress
import com.itforelead.smspaltfrom.services.Users

class UsersStub[F[_]] extends Users[F] {
  override def find(email: EmailAddress): F[Option[UserWithPassword]]                 = ???
  override def create(userParam: CreateUser, password: PasswordHash[SCrypt]): F[User] = ???
}
