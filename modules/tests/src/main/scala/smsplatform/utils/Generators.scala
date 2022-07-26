package smsplatform.utils

import com.itforelead.smspaltfrom.domain.Contact.{CreateContact, UpdateContact}
import com.itforelead.smspaltfrom.domain.Holiday.{CreateHoliday, UpdateHoliday}
import com.itforelead.smspaltfrom.domain.SMSTemplate.CreateSMSTemplate
import com.itforelead.smspaltfrom.domain.SystemSetting.{UpdateSetting, UpdateTemplateOfBirthday}
import com.itforelead.smspaltfrom.domain.TemplateCategory.CreateTemplateCategory
import com.itforelead.smspaltfrom.domain.User._
import com.itforelead.smspaltfrom.domain._
import com.itforelead.smspaltfrom.domain.custom.refinements._
import com.itforelead.smspaltfrom.domain.types._
import eu.timepit.refined.types.string.NonEmptyString
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.option
import smsplatform.utils.Arbitraries._

import java.time.{LocalDate, LocalDateTime}
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

  val messageIdGen: Gen[MessageId] =
    idGen(MessageId.apply)

  val templateIdGen: Gen[TemplateId] =
    idGen(TemplateId.apply)

  val categoryTemplateIdGen: Gen[TemplateCategoryId] =
    idGen(TemplateCategoryId.apply)

  val templateCategoryIdGen: Gen[TemplateCategoryId] =
    idGen(TemplateCategoryId.apply)

  val holidayIdGen: Gen[HolidayId] =
    idGen(HolidayId.apply)

  val usernameGen: Gen[UserName] =
    arbitrary[NonEmptyString].map(UserName.apply)

  val firstnameGen: Gen[FirstName] =
    arbitrary[NonEmptyString].map(FirstName.apply)

  val lastnameGen: Gen[LastName] =
    arbitrary[NonEmptyString].map(LastName.apply)

  val holidayNameGen: Gen[HolidayName] =
    arbitrary[NonEmptyString].map(HolidayName.apply)

  val templateCategoryNameGen: Gen[TemplateCategoryName] =
    arbitrary[NonEmptyString].map(TemplateCategoryName.apply)

  val contentGen: Gen[Content] =
    arbitrary[NonEmptyString].map(Content.apply)

  val titleGen: Gen[Title] =
    arbitrary[NonEmptyString].map(Title.apply)

  val phoneGen: Gen[Tel] = arbitrary[Tel]

  val dayOfMonthGen: Gen[DayOfMonth] = arbitrary[DayOfMonth]

  val timestampGen: Gen[LocalDateTime] = arbitrary[LocalDateTime]
  val dateGen: Gen[LocalDate]          = timestampGen.map(_.toLocalDate)

  val templateIdOptionGen: Gen[Option[TemplateId]] = option(templateIdGen)

  val passwordGen: Gen[Password] = arbitrary[Password]

  val booleanGen: Gen[Boolean] = arbitrary[Boolean]

  val emailGen: Gen[EmailAddress] = arbitrary[EmailAddress]

  val filenameGen: Gen[FileName] = arbitrary[FileName]

  val genderGen: Gen[Gender] = arbitrary[Gender]

  val roleGen: Gen[Role] = arbitrary[Role]

  val monthGen: Gen[Month] = arbitrary[Month]

  val deliveryStatusGen: Gen[DeliveryStatus] = arbitrary[DeliveryStatus]

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
      id  <- contactIdGen
      ui  <- userIdGen
      cAt <- timestampGen
      fn  <- firstnameGen
      ln  <- lastnameGen
      g   <- genderGen
      b   <- dateGen
      p   <- phoneGen
    } yield Contact(id, ui, cAt, fn, ln, g, b, p)

  val createContactGen: Gen[CreateContact] =
    for {
      fn <- firstnameGen
      ln <- lastnameGen
      g  <- genderGen
      b  <- dateGen
      p  <- phoneGen
    } yield CreateContact(fn, ln, g, b, p)

  val updateContactGen: Gen[UpdateContact] =
    for {
      i  <- contactIdGen
      fn <- firstnameGen
      ln <- lastnameGen
      g  <- genderGen
      b  <- dateGen
      p  <- phoneGen
    } yield UpdateContact(i, fn, ln, g, b, p)

  val holidayGen: Gen[Holiday] =
    for {
      id <- holidayIdGen
      n  <- holidayNameGen
      d  <- dayOfMonthGen
      m  <- monthGen
      sw <- option(templateIdGen)
      sm <- option(templateIdGen)
    } yield Holiday(id, n, d, m, sw, sm)

  val systemSettingsGen: Gen[SystemSetting] =
    for {
      asb <- booleanGen
      ash <- booleanGen
      dth <- booleanGen
      sm  <- option(templateIdGen)
      sw  <- option(templateIdGen)
    } yield SystemSetting(asb, ash, dth, sm, sw)

  val updateSystemSettingsGen: Gen[UpdateSetting] =
    for {
      asb <- booleanGen
      ash <- booleanGen
      dth <- booleanGen
    } yield UpdateSetting(asb, ash, dth)

  val updateTemplateOfBirthdayGen: Gen[UpdateTemplateOfBirthday] =
    for {
      sm <- option(templateIdGen)
      sw <- option(templateIdGen)
    } yield UpdateTemplateOfBirthday(sm, sw)

  val createHolidayGen: Gen[CreateHoliday] =
    for {
      n <- holidayNameGen
      d <- dayOfMonthGen
      m <- monthGen
    } yield CreateHoliday(n, d, m)

  val updateHolidayGen: Gen[UpdateHoliday] =
    for {
      i <- holidayIdGen
      n <- holidayNameGen
      d <- dayOfMonthGen
      m <- monthGen
    } yield UpdateHoliday(i, n, d, m)

  val templateCategoryGen: Gen[TemplateCategory] =
    for {
      tcid <- templateCategoryIdGen
      tcn  <- templateCategoryNameGen
    } yield TemplateCategory(tcid, tcn)

  val createTemplateCategoryGen: Gen[CreateTemplateCategory] =
    for {
      n <- templateCategoryNameGen
    } yield CreateTemplateCategory(n)

  val smsTemplateGen: Gen[SMSTemplate] =
    for {
      id   <- templateIdGen
      tcid <- templateCategoryIdGen
      t    <- titleGen
      c    <- contentGen
      g    <- genderGen
    } yield SMSTemplate(id, tcid, t, c, g)

  val createSMSTemplateGen: Gen[CreateSMSTemplate] =
    for {
      tcid <- templateCategoryIdGen
      t    <- titleGen
      c    <- contentGen
      g    <- genderGen
    } yield CreateSMSTemplate(tcid, t, c, g)

  val messageGen: Gen[Message] =
    for {
      id  <- messageIdGen
      cId <- contactIdGen
      tId <- templateIdGen
      sd  <- timestampGen
      ds  <- deliveryStatusGen
    } yield Message(id, cId, tId, sd, ds)

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
