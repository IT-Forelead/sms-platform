package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.domain.SystemSetting.UpdateTemplateOfBirthday
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object SystemSettingSql {

  private val Columns = bool ~ bool ~ bool ~ templateId.opt ~ templateId.opt

  val encoder: Encoder[SystemSetting] =
    Columns.contramap(c => c.autoSendBirthday ~ c.autoSendHoliday ~ c.darkTheme ~ c.smsMenId ~ c.smsWomenId)

  val decoder: Decoder[SystemSetting] =
    Columns.map { case autoSendBirthday ~ autoSendHoliday ~ darkTheme ~ smsMenId ~ smsWomenId =>
      SystemSetting(autoSendBirthday, autoSendHoliday, darkTheme, smsMenId, smsWomenId)
    }

  val select: Query[Void, SystemSetting] =
    sql"""SELECT * FROM system_settings LIMIT 1""".query(decoder)

  val updateSql: Query[SystemSetting, SystemSetting] =
    sql"""UPDATE system_settings
         SET auto_send_b = $bool,
         auto_send_h = $bool,
         dark_mode = $bool RETURNING *"""
      .query(decoder)
      .contramap[SystemSetting](s => s.autoSendBirthday ~ s.autoSendHoliday ~ s.darkTheme)

  val updateTemplatesSql: Query[UpdateTemplateOfBirthday, SystemSetting] =
    sql"""UPDATE system_settings SET sms_men_id = ${templateId.opt}, sms_women_id = ${templateId.opt} RETURNING *"""
      .query(decoder)
      .contramap[UpdateTemplateOfBirthday](s => s.smsMenId ~ s.smsWomenId)

}
