package com.itforelead.smspaltfrom.services.sql

import com.itforelead.smspaltfrom.domain.SystemSetting
import com.itforelead.smspaltfrom.domain.SystemSetting.{UpdateSetting, UpdateTemplateOfBirthday}
import com.itforelead.smspaltfrom.domain.types.UserId
import com.itforelead.smspaltfrom.services.sql.SMSTemplateSql.templateId
import com.itforelead.smspaltfrom.services.sql.UserSQL.userId
import skunk._
import skunk.codec.all.bool
import skunk.implicits._

object SystemSettingSql {

  private val Columns = userId ~ bool ~ bool ~ bool ~ templateId.opt ~ templateId.opt

  val encoder: Encoder[SystemSetting] =
    Columns.contramap(c => c.userId ~ c.autoSendBirthday ~ c.autoSendHoliday ~ c.darkTheme ~ c.smsMenId ~ c.smsWomenId)

  val decoder: Decoder[SystemSetting] =
    Columns.map { case userId ~ autoSendBirthday ~ autoSendHoliday ~ darkTheme ~ smsMenId ~ smsWomenId =>
      SystemSetting(userId, autoSendBirthday, autoSendHoliday, darkTheme, smsMenId, smsWomenId)
    }

  val select: Query[UserId, SystemSetting] =
    sql"""SELECT * FROM system_settings WHERE user_id = $userId LIMIT 1""".query(decoder)

  val updateSql: Query[UpdateSetting ~ UserId, SystemSetting] =
    sql"""UPDATE system_settings
         SET auto_send_b = $bool,
         auto_send_h = $bool,
         dark_mode = $bool WHERE user_id = $userId RETURNING *"""
      .query(decoder)
      .contramap[UpdateSetting ~ UserId] { case (s ~ ui) => s.autoSendBirthday ~ s.autoSendHoliday ~ s.darkTheme ~ ui }

  val updateTemplatesSql: Query[UpdateTemplateOfBirthday ~ UserId, SystemSetting] =
    sql"""UPDATE system_settings SET sms_men_id = ${templateId.opt}, sms_women_id = ${templateId.opt}
          WHERE user_id = $userId RETURNING *"""
      .query(decoder)
      .contramap[UpdateTemplateOfBirthday ~ UserId] { case (s ~ ui) => s.smsMenId ~ s.smsWomenId ~ ui }

}
