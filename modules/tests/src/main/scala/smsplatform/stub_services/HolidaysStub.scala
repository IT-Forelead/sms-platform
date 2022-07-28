package smsplatform.stub_services

import com.itforelead.smspaltfrom.domain.Holiday
import com.itforelead.smspaltfrom.domain.Holiday.{CreateHoliday, UpdateHoliday, UpdateTemplateInHoliday}
import com.itforelead.smspaltfrom.domain.types.{HolidayId, UserId}
import com.itforelead.smspaltfrom.services.Holidays

class HolidaysStub[F[_]] extends Holidays[F] {
  override def create(userId: UserId, holidayParam: CreateHoliday): F[Holiday]                       = ???
  override def holidays(userId: UserId): F[List[Holiday]]                                            = ???
  override def update(userId: UserId, holiday: UpdateHoliday): F[Holiday]                            = ???
  override def updateTemplateInHoliday(userId: UserId, holiday: UpdateTemplateInHoliday): F[Holiday] = ???
  override def delete(id: HolidayId, userId: UserId): F[Unit]                                        = ???
  override def holidaysOfToday(userId: UserId): F[List[Holiday]]                                     = ???
}
