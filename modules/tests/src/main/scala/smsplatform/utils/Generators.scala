package smsplatform.utils

import eu.timepit.refined.scalacheck.string._
import eu.timepit.refined.types.string.NonEmptyString
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import com.itforelead.smspaltfrom.domain.User._
import com.itforelead.smspaltfrom.domain.custom.refinements.{EmailAddress, FileName, Password, Tel}
import com.itforelead.smspaltfrom.domain.types.{Birthday, ContactId, CreatedAt, FirstName, LastName, UserId, UserName}
import com.itforelead.smspaltfrom.domain.{Contact, Credentials, Gender, Role, User}
import Arbitraries._
import com.itforelead.smspaltfrom.domain.Contact.CreateContact

import java.time.LocalDateTime
import java.util.UUID

object Generators {

  def nonEmptyStringGen(min: Int, max: Int): Gen[String] =
    Gen
      .chooseNum(min, max)
      .flatMap { n =>
        Gen.buildableOfN[String, Char](n, Gen.alphaChar)
      }

  def nonEmptyAlphaNumGen(min: Int, max: Int): Gen[String] =
    Gen
      .chooseNum(min, max)
      .flatMap { n =>
        Gen.buildableOfN[String, Char](n, Gen.alphaNumChar)
      }

  def numberGen(length: Int): Gen[String] = Gen.buildableOfN[String, Char](length, Gen.numChar)

  def idGen[A](f: UUID => A): Gen[A] =
    Gen.uuid.map(f)

  val userIdGen: Gen[UserId] =
    idGen(UserId.apply)

  val contactIdGen: Gen[ContactId] =
    idGen(ContactId.apply)

  val usernameGen: Gen[UserName] =
    arbitrary[NonEmptyString].map(UserName.apply)

  val firstnameGen: Gen[FirstName] =
    arbitrary[NonEmptyString].map(FirstName.apply)

  val lastnameGen: Gen[LastName] =
    arbitrary[NonEmptyString].map(LastName.apply)

  val phoneGen: Gen[Tel] = arbitrary[Tel]

  val birthdayGen: Gen[Birthday] = arbitrary[LocalDateTime].map(Birthday.apply)

  val createdAtGen: Gen[CreatedAt] = arbitrary[LocalDateTime].map(CreatedAt.apply)

  val passwordGen: Gen[Password] = arbitrary[Password]

  val booleanGen: Gen[Boolean] = arbitrary[Boolean]

  val emailGen: Gen[EmailAddress] = arbitrary[EmailAddress]

  val filenameGen: Gen[FileName] = arbitrary[FileName]

  val genderGen: Gen[Gender] = arbitrary[Gender]

  val roleGen: Gen[Role] = arbitrary[Role]

  val userGen: Gen[User] =
    for {
      i <- userIdGen
      n <- usernameGen
      e <- emailGen
      g <- genderGen
      r <- roleGen
    } yield User(i, n, e, g, r)

  val contactGen: Gen[Contact] =
    for {
      id <- contactIdGen
      cAt <- createdAtGen
      fn <- firstnameGen
      ln <- lastnameGen
      b <- birthdayGen
      p <- phoneGen
    } yield Contact(id, cAt, fn, ln, b, p)

  val createContactGen: Gen[CreateContact] =
    for {
      fn <- firstnameGen
      ln <- lastnameGen
      b <- birthdayGen
      p <- phoneGen
    } yield CreateContact(fn, ln, b, p)

  val userCredentialGen: Gen[Credentials] =
    for {
      e <- emailGen
      p <- passwordGen
    } yield Credentials(e, p)

  val createUserGen: Gen[CreateUser] =
    for {
      u <- usernameGen
      e <- emailGen
      g <- genderGen
      p <- passwordGen
    } yield CreateUser(u, e, g, p)
}
