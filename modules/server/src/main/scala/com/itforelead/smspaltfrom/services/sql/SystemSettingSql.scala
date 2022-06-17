package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object SystemSettingSql {

  private val Columns =
    templateId.opt ~ templateId.opt ~ bool ~ bool ~ bool

  val encoder: Encoder[SystemSetting] =
    Columns.contramap(c => c.smsWomenId ~ c.smsMenId ~ c.autoSendBirthday ~ c.autoSendHoliday ~ c.darkTheme)

  val decoder: Decoder[SystemSetting] =
    Columns.map { case smsWomenId ~ smsMenId ~ autoSendBirthday ~ autoSendHoliday ~ darkTheme =>
      SystemSetting(smsWomenId, smsMenId, autoSendBirthday, autoSendHoliday, darkTheme)
    }

  val select: Query[Void, SystemSetting] =
    sql"""SELECT * FROM system_settings LIMIT 1""".query(decoder)

  val updateSql: Query[SystemSetting, SystemSetting] =
    sql"""UPDATE system_settings
         SET sms_women_id = ${templateId.opt},
         sms_men_id = ${templateId.opt},
         auto_send_b = $bool,
         auto_send_h = $bool,
         dark_mode = $bool RETURNING *"""
      .query(decoder)
      .contramap[SystemSetting](c => c.smsWomenId ~ c.smsMenId ~ c.autoSendBirthday ~ c.autoSendHoliday ~ c.darkTheme)

}
