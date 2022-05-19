package com.itforelead.smspaltfrom.utils.ciris

import _root_.ciris.ConfigDecoder
import com.itforelead.smspaltfrom.utils.derevo.Derive

object configDecoder extends Derive[Decoder.Id]

object Decoder {
  type Id[A] = ConfigDecoder[String, A]
}
