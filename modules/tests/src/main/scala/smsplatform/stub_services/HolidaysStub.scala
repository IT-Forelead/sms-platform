package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.Holiday.CreateHoliday
import com.itforelead.smspaltfrom.domain.types.HolidayId
import com.itforelead.smspaltfrom.services.Holidays

class HolidaysStub[F[_]] extends Holidays[F] {
  override def create(holidayParam: CreateHoliday): F[Holiday]    = ???
  override def contacts: F[List[Holiday]]                         = ???
  override def update(holiday: Holiday): F[Holiday]               = ???
  override def delete(id: HolidayId): F[Unit]                     = ???
}
