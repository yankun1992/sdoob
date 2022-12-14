package tech.yankun.sdoob.driver.mysql

import java.nio.charset.{Charset, StandardCharsets}
import scala.collection.mutable
import scala.util.Try

/**
 * MySQL collation which is a set of rules for comparing characters in a character set.
 *
 * @param mysqlCharsetName      the binding MySQL charset name for this collation.
 * @param mappedJavaCharsetName the mapped Java charset name which is mapped from the collation.
 * @param collationId           the collation Id of this collation
 */
case class MySQLCollation(mysqlCharsetName: String, mappedJavaCharsetName: String, collationId: Int)

object MySQLCollation {
  val big5_chinese_ci: MySQLCollation = MySQLCollation("big5", "Big5", 1)
  val latin2_czech_cs: MySQLCollation = MySQLCollation("latin2", "ISO8859_2", 2)
  val dec8_swedish_ci: MySQLCollation = MySQLCollation("dec8", "Cp1252", 3)
  val cp850_general_ci: MySQLCollation = MySQLCollation("cp850", "Cp850", 4)
  val latin1_german1_ci: MySQLCollation = MySQLCollation("latin1", "Cp1252", 5)
  val hp8_english_ci: MySQLCollation = MySQLCollation("hp8", "Cp1252", 6)
  val koi8r_general_ci: MySQLCollation = MySQLCollation("koi8r", "KOI8_R", 7)
  val latin1_swedish_ci: MySQLCollation = MySQLCollation("latin1", "Cp1252", 8)
  val latin2_general_ci: MySQLCollation = MySQLCollation("latin2", "ISO8859_2", 9)
  val swe7_swedish_ci: MySQLCollation = MySQLCollation("swe7", "Cp1252", 10)
  val ascii_general_ci: MySQLCollation = MySQLCollation("ascii", "US-ASCII", 11)
  val ujis_japanese_ci: MySQLCollation = MySQLCollation("ujis", "EUC_JP", 12)
  val sjis_japanese_ci: MySQLCollation = MySQLCollation("sjis", "SJIS", 13)
  val cp1251_bulgarian_ci: MySQLCollation = MySQLCollation("cp1251", "Cp1251", 14)
  val latin1_danish_ci: MySQLCollation = MySQLCollation("latin1", "Cp1252", 15)
  val hebrew_general_ci: MySQLCollation = MySQLCollation("hebrew", "ISO8859_8", 16)
  val tis620_thai_ci: MySQLCollation = MySQLCollation("tis620", "TIS620", 18)
  val euckr_korean_ci: MySQLCollation = MySQLCollation("euckr", "EUC_KR", 19)
  val latin7_estonian_cs: MySQLCollation = MySQLCollation("latin7", "ISO-8859-13", 20)
  val latin2_hungarian_ci: MySQLCollation = MySQLCollation("latin2", "ISO8859_2", 21)
  val koi8u_general_ci: MySQLCollation = MySQLCollation("koi8u", "KOI8_R", 22)
  val cp1251_ukrainian_ci: MySQLCollation = MySQLCollation("cp1251", "Cp1251", 23)
  val gb2312_chinese_ci: MySQLCollation = MySQLCollation("gb2312", "EUC_CN", 24)
  val greek_general_ci: MySQLCollation = MySQLCollation("greek", "ISO8859_7", 25)
  val cp1250_general_ci: MySQLCollation = MySQLCollation("cp1250", "Cp1250", 26)
  val latin2_croatian_ci: MySQLCollation = MySQLCollation("latin2", "ISO8859_2", 27)
  val gbk_chinese_ci: MySQLCollation = MySQLCollation("gbk", "GBK", 28)
  val cp1257_lithuanian_ci: MySQLCollation = MySQLCollation("cp1257", "Cp1257", 29)
  val latin5_turkish_ci: MySQLCollation = MySQLCollation("latin5", "ISO8859_9", 30)
  val latin1_german2_ci: MySQLCollation = MySQLCollation("latin1", "Cp1252", 31)
  val armscii8_general_ci: MySQLCollation = MySQLCollation("armscii8", "Cp1252", 32)
  val utf8_general_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 33)
  val cp1250_czech_cs: MySQLCollation = MySQLCollation("cp1250", "Cp1250", 34)
  val ucs2_general_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 35)
  val cp866_general_ci: MySQLCollation = MySQLCollation("cp866", "Cp866", 36)
  val keybcs2_general_ci: MySQLCollation = MySQLCollation("keybcs2", "Cp852", 37)
  val macce_general_ci: MySQLCollation = MySQLCollation("macce", "MacCentralEurope", 38)
  val macroman_general_ci: MySQLCollation = MySQLCollation("macroman", "MacRoman", 39)
  val cp852_general_ci: MySQLCollation = MySQLCollation("cp852", "Cp852", 40)
  val latin7_general_ci: MySQLCollation = MySQLCollation("latin7", "ISO-8859-13", 41)
  val latin7_general_cs: MySQLCollation = MySQLCollation("latin7", "ISO-8859-13", 42)
  val macce_bin: MySQLCollation = MySQLCollation("macce", "MacCentralEurope", 43)
  val cp1250_croatian_ci: MySQLCollation = MySQLCollation("cp1250", "Cp1250", 44)
  val utf8mb4_general_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 45)
  val utf8mb4_bin: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 46)
  val latin1_bin: MySQLCollation = MySQLCollation("latin1", "Cp1252", 47)
  val latin1_general_ci: MySQLCollation = MySQLCollation("latin1", "Cp1252", 48)
  val latin1_general_cs: MySQLCollation = MySQLCollation("latin1", "Cp1252", 49)
  val cp1251_bin: MySQLCollation = MySQLCollation("cp1251", "Cp1251", 50)
  val cp1251_general_ci: MySQLCollation = MySQLCollation("cp1251", "Cp1251", 51)
  val cp1251_general_cs: MySQLCollation = MySQLCollation("cp1251", "Cp1251", 52)
  val macroman_bin: MySQLCollation = MySQLCollation("macroman", "MacRoman", 53)
  val utf16_general_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 54)
  val utf16_bin: MySQLCollation = MySQLCollation("utf16", "UTF-16", 55)
  val utf16le_general_ci: MySQLCollation = MySQLCollation("utf16le", "UTF-16LE", 56)
  val cp1256_general_ci: MySQLCollation = MySQLCollation("cp1256", "Cp1256", 57)
  val cp1257_bin: MySQLCollation = MySQLCollation("cp1257", "Cp1257", 58)
  val cp1257_general_ci: MySQLCollation = MySQLCollation("cp1257", "Cp1257", 59)
  val utf32_general_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 60)
  val utf32_bin: MySQLCollation = MySQLCollation("utf32", "UTF-32", 61)
  val utf16le_bin: MySQLCollation = MySQLCollation("utf16le", "UTF-16LE", 62)
  val binary: MySQLCollation = MySQLCollation("binary", "ISO8859_1", 63)
  val armscii8_bin: MySQLCollation = MySQLCollation("armscii8", "Cp1252", 64)
  val ascii_bin: MySQLCollation = MySQLCollation("ascii", "US-ASCII", 65)
  val cp1250_bin: MySQLCollation = MySQLCollation("cp1250", "Cp1250", 66)
  val cp1256_bin: MySQLCollation = MySQLCollation("cp1256", "Cp1256", 67)
  val cp866_bin: MySQLCollation = MySQLCollation("cp866", "Cp866", 68)
  val dec8_bin: MySQLCollation = MySQLCollation("dec8", "Cp1252", 69)
  val greek_bin: MySQLCollation = MySQLCollation("greek", "ISO8859_7", 70)
  val hebrew_bin: MySQLCollation = MySQLCollation("hebrew", "ISO8859_8", 71)
  val hp8_bin: MySQLCollation = MySQLCollation("hp8", "Cp1252", 72)
  val keybcs2_bin: MySQLCollation = MySQLCollation("keybcs2", "Cp852", 73)
  val koi8r_bin: MySQLCollation = MySQLCollation("koi8r", "KOI8_R", 74)
  val koi8u_bin: MySQLCollation = MySQLCollation("koi8u", "KOI8_R", 75)
  val latin2_bin: MySQLCollation = MySQLCollation("latin2", "ISO8859_2", 77)
  val latin5_bin: MySQLCollation = MySQLCollation("latin5", "ISO8859_9", 78)
  val latin7_bin: MySQLCollation = MySQLCollation("latin7", "ISO-8859-13", 79)
  val cp850_bin: MySQLCollation = MySQLCollation("cp850", "Cp850", 80)
  val cp852_bin: MySQLCollation = MySQLCollation("cp852", "Cp852", 81)
  val swe7_bin: MySQLCollation = MySQLCollation("swe7", "Cp1252", 82)
  val utf8_bin: MySQLCollation = MySQLCollation("utf8", "UTF-8", 83)
  val big5_bin: MySQLCollation = MySQLCollation("big5", "Big5", 84)
  val euckr_bin: MySQLCollation = MySQLCollation("euckr", "EUC_KR", 85)
  val gb2312_bin: MySQLCollation = MySQLCollation("gb2312", "EUC_CN", 86)
  val gbk_bin: MySQLCollation = MySQLCollation("gbk", "GBK", 87)
  val sjis_bin: MySQLCollation = MySQLCollation("sjis", "SJIS", 88)
  val tis620_bin: MySQLCollation = MySQLCollation("tis620", "TIS620", 89)
  val ucs2_bin: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 90)
  val ujis_bin: MySQLCollation = MySQLCollation("ujis", "EUC_JP", 91)
  val geostd8_general_ci: MySQLCollation = MySQLCollation("geostd8", "Cp1252", 92)
  val geostd8_bin: MySQLCollation = MySQLCollation("geostd8", "Cp1252", 93)
  val latin1_spanish_ci: MySQLCollation = MySQLCollation("latin1", "Cp1252", 94)
  val cp932_japanese_ci: MySQLCollation = MySQLCollation("cp932", "Cp932", 95)
  val cp932_bin: MySQLCollation = MySQLCollation("cp932", "Cp932", 96)
  val eucjpms_japanese_ci: MySQLCollation = MySQLCollation("eucjpms", "EUC_JP_Solaris", 97)
  val eucjpms_bin: MySQLCollation = MySQLCollation("eucjpms", "EUC_JP_Solaris", 98)
  val cp1250_polish_ci: MySQLCollation = MySQLCollation("cp1250", "Cp1250", 99)
  val utf16_unicode_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 101)
  val utf16_icelandic_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 102)
  val utf16_latvian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 103)
  val utf16_romanian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 104)
  val utf16_slovenian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 105)
  val utf16_polish_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 106)
  val utf16_estonian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 107)
  val utf16_spanish_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 108)
  val utf16_swedish_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 109)
  val utf16_turkish_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 110)
  val utf16_czech_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 111)
  val utf16_danish_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 112)
  val utf16_lithuanian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 113)
  val utf16_slovak_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 114)
  val utf16_spanish2_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 115)
  val utf16_roman_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 116)
  val utf16_persian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 117)
  val utf16_esperanto_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 118)
  val utf16_hungarian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 119)
  val utf16_sinhala_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 120)
  val utf16_german2_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 121)
  val utf16_croatian_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 122)
  val utf16_unicode_520_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 123)
  val utf16_vietnamese_ci: MySQLCollation = MySQLCollation("utf16", "UTF-16", 124)
  val ucs2_unicode_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 128)
  val ucs2_icelandic_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 129)
  val ucs2_latvian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 130)
  val ucs2_romanian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 131)
  val ucs2_slovenian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 132)
  val ucs2_polish_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 133)
  val ucs2_estonian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 134)
  val ucs2_spanish_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 135)
  val ucs2_swedish_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 136)
  val ucs2_turkish_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 137)
  val ucs2_czech_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 138)
  val ucs2_danish_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 139)
  val ucs2_lithuanian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 140)
  val ucs2_slovak_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 141)
  val ucs2_spanish2_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 142)
  val ucs2_roman_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 143)
  val ucs2_persian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 144)
  val ucs2_esperanto_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 145)
  val ucs2_hungarian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 146)
  val ucs2_sinhala_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 147)
  val ucs2_german2_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 148)
  val ucs2_croatian_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 149)
  val ucs2_unicode_520_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 150)
  val ucs2_vietnamese_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 151)
  val ucs2_general_mysql500_ci: MySQLCollation = MySQLCollation("ucs2", "UnicodeBig", 159)
  val utf32_unicode_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 160)
  val utf32_icelandic_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 161)
  val utf32_latvian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 162)
  val utf32_romanian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 163)
  val utf32_slovenian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 164)
  val utf32_polish_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 165)
  val utf32_estonian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 166)
  val utf32_spanish_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 167)
  val utf32_swedish_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 168)
  val utf32_turkish_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 169)
  val utf32_czech_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 170)
  val utf32_danish_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 171)
  val utf32_lithuanian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 172)
  val utf32_slovak_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 173)
  val utf32_spanish2_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 174)
  val utf32_roman_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 175)
  val utf32_persian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 176)
  val utf32_esperanto_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 177)
  val utf32_hungarian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 178)
  val utf32_sinhala_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 179)
  val utf32_german2_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 180)
  val utf32_croatian_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 181)
  val utf32_unicode_520_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 182)
  val utf32_vietnamese_ci: MySQLCollation = MySQLCollation("utf32", "UTF-32", 183)
  val utf8_unicode_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 192)
  val utf8_icelandic_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 193)
  val utf8_latvian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 194)
  val utf8_romanian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 195)
  val utf8_slovenian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 196)
  val utf8_polish_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 197)
  val utf8_estonian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 198)
  val utf8_spanish_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 199)
  val utf8_swedish_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 200)
  val utf8_turkish_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 201)
  val utf8_czech_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 202)
  val utf8_danish_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 203)
  val utf8_lithuanian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 204)
  val utf8_slovak_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 205)
  val utf8_spanish2_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 206)
  val utf8_roman_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 207)
  val utf8_persian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 208)
  val utf8_esperanto_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 209)
  val utf8_hungarian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 210)
  val utf8_sinhala_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 211)
  val utf8_german2_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 212)
  val utf8_croatian_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 213)
  val utf8_unicode_520_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 214)
  val utf8_vietnamese_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 215)
  val utf8_general_mysql500_ci: MySQLCollation = MySQLCollation("utf8", "UTF-8", 223)
  val utf8mb4_unicode_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 224)
  val utf8mb4_icelandic_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 225)
  val utf8mb4_latvian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 226)
  val utf8mb4_romanian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 227)
  val utf8mb4_slovenian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 228)
  val utf8mb4_polish_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 229)
  val utf8mb4_estonian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 230)
  val utf8mb4_spanish_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 231)
  val utf8mb4_swedish_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 232)
  val utf8mb4_turkish_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 233)
  val utf8mb4_czech_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 234)
  val utf8mb4_danish_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 235)
  val utf8mb4_lithuanian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 236)
  val utf8mb4_slovak_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 237)
  val utf8mb4_spanish2_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 238)
  val utf8mb4_roman_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 239)
  val utf8mb4_persian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 240)
  val utf8mb4_esperanto_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 241)
  val utf8mb4_hungarian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 242)
  val utf8mb4_sinhala_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 243)
  val utf8mb4_german2_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 244)
  val utf8mb4_croatian_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 245)
  val utf8mb4_unicode_520_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 246)
  val utf8mb4_vietnamese_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 247)
  val gb18030_chinese_ci: MySQLCollation = MySQLCollation("gb18030", "GB18030", 248)
  val gb18030_bin: MySQLCollation = MySQLCollation("gb18030", "GB18030", 249)
  val gb18030_unicode_520_ci: MySQLCollation = MySQLCollation("gb18030", "GB18030", 250)
  val utf8mb4_0900_ai_ci: MySQLCollation = MySQLCollation("utf8mb4", "UTF-8", 255)

  def values: Array[MySQLCollation] = Array(big5_chinese_ci, latin2_czech_cs, dec8_swedish_ci, cp850_general_ci,
    latin1_german1_ci, hp8_english_ci, koi8r_general_ci, latin1_swedish_ci, latin2_general_ci, swe7_swedish_ci,
    ascii_general_ci, ujis_japanese_ci, sjis_japanese_ci, cp1251_bulgarian_ci, latin1_danish_ci, hebrew_general_ci,
    tis620_thai_ci, euckr_korean_ci, latin7_estonian_cs, latin2_hungarian_ci, koi8u_general_ci, cp1251_ukrainian_ci,
    gb2312_chinese_ci, greek_general_ci, cp1250_general_ci, latin2_croatian_ci, gbk_chinese_ci, cp1257_lithuanian_ci,
    latin5_turkish_ci, latin1_german2_ci, armscii8_general_ci, utf8_general_ci, cp1250_czech_cs, ucs2_general_ci,
    cp866_general_ci, keybcs2_general_ci, macce_general_ci, macroman_general_ci, cp852_general_ci, latin7_general_ci,
    latin7_general_cs, macce_bin, cp1250_croatian_ci, utf8mb4_general_ci, utf8mb4_bin, latin1_bin, latin1_general_ci,
    latin1_general_cs, cp1251_bin, cp1251_general_ci, cp1251_general_cs, macroman_bin, utf16_general_ci, utf16_bin,
    utf16le_general_ci, cp1256_general_ci, cp1257_bin, cp1257_general_ci, utf32_general_ci, utf32_bin, utf16le_bin,
    binary, armscii8_bin, ascii_bin, cp1250_bin, cp1256_bin, cp866_bin, dec8_bin, greek_bin, hebrew_bin, hp8_bin,
    keybcs2_bin, koi8r_bin, koi8u_bin, latin2_bin, latin5_bin, latin7_bin, cp850_bin, cp852_bin, swe7_bin, utf8_bin,
    big5_bin, euckr_bin, gb2312_bin, gbk_bin, sjis_bin, tis620_bin, ucs2_bin, ujis_bin, geostd8_general_ci,
    geostd8_bin, latin1_spanish_ci, cp932_japanese_ci, cp932_bin, eucjpms_japanese_ci, eucjpms_bin, cp1250_polish_ci,
    utf16_unicode_ci, utf16_icelandic_ci, utf16_latvian_ci, utf16_romanian_ci, utf16_slovenian_ci, utf16_polish_ci,
    utf16_estonian_ci, utf16_spanish_ci, utf16_swedish_ci, utf16_turkish_ci, utf16_czech_ci, utf16_danish_ci,
    utf16_lithuanian_ci, utf16_slovak_ci, utf16_spanish2_ci, utf16_roman_ci, utf16_persian_ci, utf16_esperanto_ci,
    utf16_hungarian_ci, utf16_sinhala_ci, utf16_german2_ci, utf16_croatian_ci, utf16_unicode_520_ci, utf16_vietnamese_ci,
    ucs2_unicode_ci, ucs2_icelandic_ci, ucs2_latvian_ci, ucs2_romanian_ci, ucs2_slovenian_ci, ucs2_polish_ci,
    ucs2_estonian_ci, ucs2_spanish_ci, ucs2_swedish_ci, ucs2_turkish_ci, ucs2_czech_ci, ucs2_danish_ci, ucs2_lithuanian_ci,
    ucs2_slovak_ci, ucs2_spanish2_ci, ucs2_roman_ci, ucs2_persian_ci, ucs2_esperanto_ci, ucs2_hungarian_ci,
    ucs2_sinhala_ci, ucs2_german2_ci, ucs2_croatian_ci, ucs2_unicode_520_ci, ucs2_vietnamese_ci, ucs2_general_mysql500_ci,
    utf32_unicode_ci, utf32_icelandic_ci, utf32_latvian_ci, utf32_romanian_ci, utf32_slovenian_ci, utf32_polish_ci,
    utf32_estonian_ci, utf32_spanish_ci, utf32_swedish_ci, utf32_turkish_ci, utf32_czech_ci, utf32_danish_ci,
    utf32_lithuanian_ci, utf32_slovak_ci, utf32_spanish2_ci, utf32_roman_ci, utf32_persian_ci, utf32_esperanto_ci,
    utf32_hungarian_ci, utf32_sinhala_ci, utf32_german2_ci, utf32_croatian_ci, utf32_unicode_520_ci, utf32_vietnamese_ci,
    utf8_unicode_ci, utf8_icelandic_ci, utf8_latvian_ci, utf8_romanian_ci, utf8_slovenian_ci, utf8_polish_ci,
    utf8_estonian_ci, utf8_spanish_ci, utf8_swedish_ci, utf8_turkish_ci, utf8_czech_ci, utf8_danish_ci, utf8_lithuanian_ci,
    utf8_slovak_ci, utf8_spanish2_ci, utf8_roman_ci, utf8_persian_ci, utf8_esperanto_ci, utf8_hungarian_ci, utf8_sinhala_ci,
    utf8_german2_ci, utf8_croatian_ci, utf8_unicode_520_ci, utf8_vietnamese_ci, utf8_general_mysql500_ci, utf8mb4_unicode_ci,
    utf8mb4_icelandic_ci, utf8mb4_latvian_ci, utf8mb4_romanian_ci, utf8mb4_slovenian_ci, utf8mb4_polish_ci, utf8mb4_estonian_ci,
    utf8mb4_spanish_ci, utf8mb4_swedish_ci, utf8mb4_turkish_ci, utf8mb4_czech_ci, utf8mb4_danish_ci, utf8mb4_lithuanian_ci,
    utf8mb4_slovak_ci, utf8mb4_spanish2_ci, utf8mb4_roman_ci, utf8mb4_persian_ci, utf8mb4_esperanto_ci, utf8mb4_hungarian_ci,
    utf8mb4_sinhala_ci, utf8mb4_german2_ci, utf8mb4_croatian_ci, utf8mb4_unicode_520_ci, utf8mb4_vietnamese_ci, gb18030_chinese_ci,
    gb18030_bin, gb18030_unicode_520_ci, utf8mb4_0900_ai_ci)

  val SUPPORTED_COLLATION_NAMES: Array[String] = values.map(name)

  val SUPPORTED_CHARSET_NAMES: Array[String] = values.map(_.mysqlCharsetName)

  val charsetToDefaultCollationMapping = mutable.Map(
    ("big5", "big5_chinese_ci"),
    ("dec8", "dec8_swedish_ci"),
    ("cp850", "cp850_general_ci"),
    ("hp8", "hp8_english_ci"),
    ("koi8r", "koi8r_general_ci"),
    ("latin1", "latin1_swedish_ci"),
    ("latin2", "latin2_general_ci"),
    ("swe7", "swe7_swedish_ci"),
    ("ascii", "ascii_general_ci"),
    ("ujis", "ujis_japanese_ci"),
    ("sjis", "sjis_japanese_ci"),
    ("hebrew", "hebrew_general_ci"),
    ("tis620", "tis620_thai_ci"),
    ("euckr", "euckr_korean_ci"),
    ("koi8u", "koi8u_general_ci"),
    ("gb2312", "gb2312_chinese_ci"),
    ("greek", "greek_general_ci"),
    ("cp1250", "cp1250_general_ci"),
    ("gbk", "gbk_chinese_ci"),
    ("latin5", "latin5_turkish_ci"),
    ("armscii8", "armscii8_general_ci"),
    ("utf8", "utf8_general_ci"),
    ("ucs2", "ucs2_general_ci"),
    ("cp866", "cp866_general_ci"),
    ("keybcs2", "keybcs2_general_ci"),
    ("macce", "macce_general_ci"),
    ("macroman", "macroman_general_ci"),
    ("cp852", "cp852_general_ci"),
    ("latin7", "latin7_general_ci"),
    ("utf8mb4", "utf8mb4_general_ci"),
    ("cp1251", "cp1251_general_ci"),
    ("utf16", "utf16_general_ci"),
    ("utf16le", "utf16le_general_ci"),
    ("cp1256", "cp1256_general_ci"),
    ("cp1257", "cp1257_general_ci"),
    ("utf32", "utf32_general_ci"),
    ("binary", "binary"),
    ("geostd8", "geostd8_general_ci"),
    ("cp932", "cp932_japanese_ci"),
    ("eucjpms", "eucjpms_japanese_ci"),
    ("gb18030", "gb18030_chinese_ci")
  )

  val idToJavaCharsetMapping: Map[Int, Charset] =
    values.map(collation => Try {
      collation.collationId -> Charset.forName(collation.mappedJavaCharsetName)
    }.toOption)
      .filter(_.nonEmpty).map(_.get).toMap

  val DEFAULT_COLLATION: MySQLCollation = utf8mb4_general_ci

  def valueOfName(collationName: String): MySQLCollation = {
    collationName match {
      case "big5_chinese_ci" => big5_chinese_ci
      case "latin2_czech_cs" => latin2_czech_cs
      case "dec8_swedish_ci" => dec8_swedish_ci
      case "cp850_general_ci" => cp850_general_ci
      case "latin1_german1_ci" => latin1_german1_ci
      case "hp8_english_ci" => hp8_english_ci
      case "koi8r_general_ci" => koi8r_general_ci
      case "latin1_swedish_ci" => latin1_swedish_ci
      case "latin2_general_ci" => latin2_general_ci
      case "swe7_swedish_ci" => swe7_swedish_ci
      case "ascii_general_ci" => ascii_general_ci
      case "ujis_japanese_ci" => ujis_japanese_ci
      case "sjis_japanese_ci" => sjis_japanese_ci
      case "cp1251_bulgarian_ci" => cp1251_bulgarian_ci
      case "latin1_danish_ci" => latin1_danish_ci
      case "hebrew_general_ci" => hebrew_general_ci
      case "tis620_thai_ci" => tis620_thai_ci
      case "euckr_korean_ci" => euckr_korean_ci
      case "latin7_estonian_cs" => latin7_estonian_cs
      case "latin2_hungarian_ci" => latin2_hungarian_ci
      case "koi8u_general_ci" => koi8u_general_ci
      case "cp1251_ukrainian_ci" => cp1251_ukrainian_ci
      case "gb2312_chinese_ci" => gb2312_chinese_ci
      case "greek_general_ci" => greek_general_ci
      case "cp1250_general_ci" => cp1250_general_ci
      case "latin2_croatian_ci" => latin2_croatian_ci
      case "gbk_chinese_ci" => gbk_chinese_ci
      case "cp1257_lithuanian_ci" => cp1257_lithuanian_ci
      case "latin5_turkish_ci" => latin5_turkish_ci
      case "latin1_german2_ci" => latin1_german2_ci
      case "armscii8_general_ci" => armscii8_general_ci
      case "utf8_general_ci" => utf8_general_ci
      case "cp1250_czech_cs" => cp1250_czech_cs
      case "ucs2_general_ci" => ucs2_general_ci
      case "cp866_general_ci" => cp866_general_ci
      case "keybcs2_general_ci" => keybcs2_general_ci
      case "macce_general_ci" => macce_general_ci
      case "macroman_general_ci" => macroman_general_ci
      case "cp852_general_ci" => cp852_general_ci
      case "latin7_general_ci" => latin7_general_ci
      case "latin7_general_cs" => latin7_general_cs
      case "macce_bin" => macce_bin
      case "cp1250_croatian_ci" => cp1250_croatian_ci
      case "utf8mb4_general_ci" => utf8mb4_general_ci
      case "utf8mb4_bin" => utf8mb4_bin
      case "latin1_bin" => latin1_bin
      case "latin1_general_ci" => latin1_general_ci
      case "latin1_general_cs" => latin1_general_cs
      case "cp1251_bin" => cp1251_bin
      case "cp1251_general_ci" => cp1251_general_ci
      case "cp1251_general_cs" => cp1251_general_cs
      case "macroman_bin" => macroman_bin
      case "utf16_general_ci" => utf16_general_ci
      case "utf16_bin" => utf16_bin
      case "utf16le_general_ci" => utf16le_general_ci
      case "cp1256_general_ci" => cp1256_general_ci
      case "cp1257_bin" => cp1257_bin
      case "cp1257_general_ci" => cp1257_general_ci
      case "utf32_general_ci" => utf32_general_ci
      case "utf32_bin" => utf32_bin
      case "utf16le_bin" => utf16le_bin
      case "binary" => binary
      case "armscii8_bin" => armscii8_bin
      case "ascii_bin" => ascii_bin
      case "cp1250_bin" => cp1250_bin
      case "cp1256_bin" => cp1256_bin
      case "cp866_bin" => cp866_bin
      case "dec8_bin" => dec8_bin
      case "greek_bin" => greek_bin
      case "hebrew_bin" => hebrew_bin
      case "hp8_bin" => hp8_bin
      case "keybcs2_bin" => keybcs2_bin
      case "koi8r_bin" => koi8r_bin
      case "koi8u_bin" => koi8u_bin
      case "latin2_bin" => latin2_bin
      case "latin5_bin" => latin5_bin
      case "latin7_bin" => latin7_bin
      case "cp850_bin" => cp850_bin
      case "cp852_bin" => cp852_bin
      case "swe7_bin" => swe7_bin
      case "utf8_bin" => utf8_bin
      case "big5_bin" => big5_bin
      case "euckr_bin" => euckr_bin
      case "gb2312_bin" => gb2312_bin
      case "gbk_bin" => gbk_bin
      case "sjis_bin" => sjis_bin
      case "tis620_bin" => tis620_bin
      case "ucs2_bin" => ucs2_bin
      case "ujis_bin" => ujis_bin
      case "geostd8_general_ci" => geostd8_general_ci
      case "geostd8_bin" => geostd8_bin
      case "latin1_spanish_ci" => latin1_spanish_ci
      case "cp932_japanese_ci" => cp932_japanese_ci
      case "cp932_bin" => cp932_bin
      case "eucjpms_japanese_ci" => eucjpms_japanese_ci
      case "eucjpms_bin" => eucjpms_bin
      case "cp1250_polish_ci" => cp1250_polish_ci
      case "utf16_unicode_ci" => utf16_unicode_ci
      case "utf16_icelandic_ci" => utf16_icelandic_ci
      case "utf16_latvian_ci" => utf16_latvian_ci
      case "utf16_romanian_ci" => utf16_romanian_ci
      case "utf16_slovenian_ci" => utf16_slovenian_ci
      case "utf16_polish_ci" => utf16_polish_ci
      case "utf16_estonian_ci" => utf16_estonian_ci
      case "utf16_spanish_ci" => utf16_spanish_ci
      case "utf16_swedish_ci" => utf16_swedish_ci
      case "utf16_turkish_ci" => utf16_turkish_ci
      case "utf16_czech_ci" => utf16_czech_ci
      case "utf16_danish_ci" => utf16_danish_ci
      case "utf16_lithuanian_ci" => utf16_lithuanian_ci
      case "utf16_slovak_ci" => utf16_slovak_ci
      case "utf16_spanish2_ci" => utf16_spanish2_ci
      case "utf16_roman_ci" => utf16_roman_ci
      case "utf16_persian_ci" => utf16_persian_ci
      case "utf16_esperanto_ci" => utf16_esperanto_ci
      case "utf16_hungarian_ci" => utf16_hungarian_ci
      case "utf16_sinhala_ci" => utf16_sinhala_ci
      case "utf16_german2_ci" => utf16_german2_ci
      case "utf16_croatian_ci" => utf16_croatian_ci
      case "utf16_unicode_520_ci" => utf16_unicode_520_ci
      case "utf16_vietnamese_ci" => utf16_vietnamese_ci
      case "ucs2_unicode_ci" => ucs2_unicode_ci
      case "ucs2_icelandic_ci" => ucs2_icelandic_ci
      case "ucs2_latvian_ci" => ucs2_latvian_ci
      case "ucs2_romanian_ci" => ucs2_romanian_ci
      case "ucs2_slovenian_ci" => ucs2_slovenian_ci
      case "ucs2_polish_ci" => ucs2_polish_ci
      case "ucs2_estonian_ci" => ucs2_estonian_ci
      case "ucs2_spanish_ci" => ucs2_spanish_ci
      case "ucs2_swedish_ci" => ucs2_swedish_ci
      case "ucs2_turkish_ci" => ucs2_turkish_ci
      case "ucs2_czech_ci" => ucs2_czech_ci
      case "ucs2_danish_ci" => ucs2_danish_ci
      case "ucs2_lithuanian_ci" => ucs2_lithuanian_ci
      case "ucs2_slovak_ci" => ucs2_slovak_ci
      case "ucs2_spanish2_ci" => ucs2_spanish2_ci
      case "ucs2_roman_ci" => ucs2_roman_ci
      case "ucs2_persian_ci" => ucs2_persian_ci
      case "ucs2_esperanto_ci" => ucs2_esperanto_ci
      case "ucs2_hungarian_ci" => ucs2_hungarian_ci
      case "ucs2_sinhala_ci" => ucs2_sinhala_ci
      case "ucs2_german2_ci" => ucs2_german2_ci
      case "ucs2_croatian_ci" => ucs2_croatian_ci
      case "ucs2_unicode_520_ci" => ucs2_unicode_520_ci
      case "ucs2_vietnamese_ci" => ucs2_vietnamese_ci
      case "ucs2_general_mysql500_ci" => ucs2_general_mysql500_ci
      case "utf32_unicode_ci" => utf32_unicode_ci
      case "utf32_icelandic_ci" => utf32_icelandic_ci
      case "utf32_latvian_ci" => utf32_latvian_ci
      case "utf32_romanian_ci" => utf32_romanian_ci
      case "utf32_slovenian_ci" => utf32_slovenian_ci
      case "utf32_polish_ci" => utf32_polish_ci
      case "utf32_estonian_ci" => utf32_estonian_ci
      case "utf32_spanish_ci" => utf32_spanish_ci
      case "utf32_swedish_ci" => utf32_swedish_ci
      case "utf32_turkish_ci" => utf32_turkish_ci
      case "utf32_czech_ci" => utf32_czech_ci
      case "utf32_danish_ci" => utf32_danish_ci
      case "utf32_lithuanian_ci" => utf32_lithuanian_ci
      case "utf32_slovak_ci" => utf32_slovak_ci
      case "utf32_spanish2_ci" => utf32_spanish2_ci
      case "utf32_roman_ci" => utf32_roman_ci
      case "utf32_persian_ci" => utf32_persian_ci
      case "utf32_esperanto_ci" => utf32_esperanto_ci
      case "utf32_hungarian_ci" => utf32_hungarian_ci
      case "utf32_sinhala_ci" => utf32_sinhala_ci
      case "utf32_german2_ci" => utf32_german2_ci
      case "utf32_croatian_ci" => utf32_croatian_ci
      case "utf32_unicode_520_ci" => utf32_unicode_520_ci
      case "utf32_vietnamese_ci" => utf32_vietnamese_ci
      case "utf8_unicode_ci" => utf8_unicode_ci
      case "utf8_icelandic_ci" => utf8_icelandic_ci
      case "utf8_latvian_ci" => utf8_latvian_ci
      case "utf8_romanian_ci" => utf8_romanian_ci
      case "utf8_slovenian_ci" => utf8_slovenian_ci
      case "utf8_polish_ci" => utf8_polish_ci
      case "utf8_estonian_ci" => utf8_estonian_ci
      case "utf8_spanish_ci" => utf8_spanish_ci
      case "utf8_swedish_ci" => utf8_swedish_ci
      case "utf8_turkish_ci" => utf8_turkish_ci
      case "utf8_czech_ci" => utf8_czech_ci
      case "utf8_danish_ci" => utf8_danish_ci
      case "utf8_lithuanian_ci" => utf8_lithuanian_ci
      case "utf8_slovak_ci" => utf8_slovak_ci
      case "utf8_spanish2_ci" => utf8_spanish2_ci
      case "utf8_roman_ci" => utf8_roman_ci
      case "utf8_persian_ci" => utf8_persian_ci
      case "utf8_esperanto_ci" => utf8_esperanto_ci
      case "utf8_hungarian_ci" => utf8_hungarian_ci
      case "utf8_sinhala_ci" => utf8_sinhala_ci
      case "utf8_german2_ci" => utf8_german2_ci
      case "utf8_croatian_ci" => utf8_croatian_ci
      case "utf8_unicode_520_ci" => utf8_unicode_520_ci
      case "utf8_vietnamese_ci" => utf8_vietnamese_ci
      case "utf8_general_mysql500_ci" => utf8_general_mysql500_ci
      case "utf8mb4_unicode_ci" => utf8mb4_unicode_ci
      case "utf8mb4_icelandic_ci" => utf8mb4_icelandic_ci
      case "utf8mb4_latvian_ci" => utf8mb4_latvian_ci
      case "utf8mb4_romanian_ci" => utf8mb4_romanian_ci
      case "utf8mb4_slovenian_ci" => utf8mb4_slovenian_ci
      case "utf8mb4_polish_ci" => utf8mb4_polish_ci
      case "utf8mb4_estonian_ci" => utf8mb4_estonian_ci
      case "utf8mb4_spanish_ci" => utf8mb4_spanish_ci
      case "utf8mb4_swedish_ci" => utf8mb4_swedish_ci
      case "utf8mb4_turkish_ci" => utf8mb4_turkish_ci
      case "utf8mb4_czech_ci" => utf8mb4_czech_ci
      case "utf8mb4_danish_ci" => utf8mb4_danish_ci
      case "utf8mb4_lithuanian_ci" => utf8mb4_lithuanian_ci
      case "utf8mb4_slovak_ci" => utf8mb4_slovak_ci
      case "utf8mb4_spanish2_ci" => utf8mb4_spanish2_ci
      case "utf8mb4_roman_ci" => utf8mb4_roman_ci
      case "utf8mb4_persian_ci" => utf8mb4_persian_ci
      case "utf8mb4_esperanto_ci" => utf8mb4_esperanto_ci
      case "utf8mb4_hungarian_ci" => utf8mb4_hungarian_ci
      case "utf8mb4_sinhala_ci" => utf8mb4_sinhala_ci
      case "utf8mb4_german2_ci" => utf8mb4_german2_ci
      case "utf8mb4_croatian_ci" => utf8mb4_croatian_ci
      case "utf8mb4_unicode_520_ci" => utf8mb4_unicode_520_ci
      case "utf8mb4_vietnamese_ci" => utf8mb4_vietnamese_ci
      case "gb18030_chinese_ci" => gb18030_chinese_ci
      case "gb18030_bin" => gb18030_bin
      case "gb18030_unicode_520_ci" => gb18030_unicode_520_ci
      case "utf8mb4_0900_ai_ci" => utf8mb4_0900_ai_ci
      case _ => throw new IllegalArgumentException(s"Unknown MySQL collation: [${collationName}]")
    }
  }

  def name(collation: MySQLCollation): String = {
    collation match {
      case MySQLCollation("big5", "Big5", 1) => "big5_chinese_ci"
      case MySQLCollation("latin2", "ISO8859_2", 2) => "latin2_czech_cs"
      case MySQLCollation("dec8", "Cp1252", 3) => "dec8_swedish_ci"
      case MySQLCollation("cp850", "Cp850", 4) => "cp850_general_ci"
      case MySQLCollation("latin1", "Cp1252", 5) => "latin1_german1_ci"
      case MySQLCollation("hp8", "Cp1252", 6) => "hp8_english_ci"
      case MySQLCollation("koi8r", "KOI8_R", 7) => "koi8r_general_ci"
      case MySQLCollation("latin1", "Cp1252", 8) => "latin1_swedish_ci"
      case MySQLCollation("latin2", "ISO8859_2", 9) => "latin2_general_ci"
      case MySQLCollation("swe7", "Cp1252", 10) => "swe7_swedish_ci"
      case MySQLCollation("ascii", "US-ASCII", 11) => "ascii_general_ci"
      case MySQLCollation("ujis", "EUC_JP", 12) => "ujis_japanese_ci"
      case MySQLCollation("sjis", "SJIS", 13) => "sjis_japanese_ci"
      case MySQLCollation("cp1251", "Cp1251", 14) => "cp1251_bulgarian_ci"
      case MySQLCollation("latin1", "Cp1252", 15) => "latin1_danish_ci"
      case MySQLCollation("hebrew", "ISO8859_8", 16) => "hebrew_general_ci"
      case MySQLCollation("tis620", "TIS620", 18) => "tis620_thai_ci"
      case MySQLCollation("euckr", "EUC_KR", 19) => "euckr_korean_ci"
      case MySQLCollation("latin7", "ISO-8859-13", 20) => "latin7_estonian_cs"
      case MySQLCollation("latin2", "ISO8859_2", 21) => "latin2_hungarian_ci"
      case MySQLCollation("koi8u", "KOI8_R", 22) => "koi8u_general_ci"
      case MySQLCollation("cp1251", "Cp1251", 23) => "cp1251_ukrainian_ci"
      case MySQLCollation("gb2312", "EUC_CN", 24) => "gb2312_chinese_ci"
      case MySQLCollation("greek", "ISO8859_7", 25) => "greek_general_ci"
      case MySQLCollation("cp1250", "Cp1250", 26) => "cp1250_general_ci"
      case MySQLCollation("latin2", "ISO8859_2", 27) => "latin2_croatian_ci"
      case MySQLCollation("gbk", "GBK", 28) => "gbk_chinese_ci"
      case MySQLCollation("cp1257", "Cp1257", 29) => "cp1257_lithuanian_ci"
      case MySQLCollation("latin5", "ISO8859_9", 30) => "latin5_turkish_ci"
      case MySQLCollation("latin1", "Cp1252", 31) => "latin1_german2_ci"
      case MySQLCollation("armscii8", "Cp1252", 32) => "armscii8_general_ci"
      case MySQLCollation("utf8", "UTF-8", 33) => "utf8_general_ci"
      case MySQLCollation("cp1250", "Cp1250", 34) => "cp1250_czech_cs"
      case MySQLCollation("ucs2", "UnicodeBig", 35) => "ucs2_general_ci"
      case MySQLCollation("cp866", "Cp866", 36) => "cp866_general_ci"
      case MySQLCollation("keybcs2", "Cp852", 37) => "keybcs2_general_ci"
      case MySQLCollation("macce", "MacCentralEurope", 38) => "macce_general_ci"
      case MySQLCollation("macroman", "MacRoman", 39) => "macroman_general_ci"
      case MySQLCollation("cp852", "Cp852", 40) => "cp852_general_ci"
      case MySQLCollation("latin7", "ISO-8859-13", 41) => "latin7_general_ci"
      case MySQLCollation("latin7", "ISO-8859-13", 42) => "latin7_general_cs"
      case MySQLCollation("macce", "MacCentralEurope", 43) => "macce_bin"
      case MySQLCollation("cp1250", "Cp1250", 44) => "cp1250_croatian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 45) => "utf8mb4_general_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 46) => "utf8mb4_bin"
      case MySQLCollation("latin1", "Cp1252", 47) => "latin1_bin"
      case MySQLCollation("latin1", "Cp1252", 48) => "latin1_general_ci"
      case MySQLCollation("latin1", "Cp1252", 49) => "latin1_general_cs"
      case MySQLCollation("cp1251", "Cp1251", 50) => "cp1251_bin"
      case MySQLCollation("cp1251", "Cp1251", 51) => "cp1251_general_ci"
      case MySQLCollation("cp1251", "Cp1251", 52) => "cp1251_general_cs"
      case MySQLCollation("macroman", "MacRoman", 53) => "macroman_bin"
      case MySQLCollation("utf16", "UTF-16", 54) => "utf16_general_ci"
      case MySQLCollation("utf16", "UTF-16", 55) => "utf16_bin"
      case MySQLCollation("utf16le", "UTF-16LE", 56) => "utf16le_general_ci"
      case MySQLCollation("cp1256", "Cp1256", 57) => "cp1256_general_ci"
      case MySQLCollation("cp1257", "Cp1257", 58) => "cp1257_bin"
      case MySQLCollation("cp1257", "Cp1257", 59) => "cp1257_general_ci"
      case MySQLCollation("utf32", "UTF-32", 60) => "utf32_general_ci"
      case MySQLCollation("utf32", "UTF-32", 61) => "utf32_bin"
      case MySQLCollation("utf16le", "UTF-16LE", 62) => "utf16le_bin"
      case MySQLCollation("binary", "ISO8859_1", 63) => "binary"
      case MySQLCollation("armscii8", "Cp1252", 64) => "armscii8_bin"
      case MySQLCollation("ascii", "US-ASCII", 65) => "ascii_bin"
      case MySQLCollation("cp1250", "Cp1250", 66) => "cp1250_bin"
      case MySQLCollation("cp1256", "Cp1256", 67) => "cp1256_bin"
      case MySQLCollation("cp866", "Cp866", 68) => "cp866_bin"
      case MySQLCollation("dec8", "Cp1252", 69) => "dec8_bin"
      case MySQLCollation("greek", "ISO8859_7", 70) => "greek_bin"
      case MySQLCollation("hebrew", "ISO8859_8", 71) => "hebrew_bin"
      case MySQLCollation("hp8", "Cp1252", 72) => "hp8_bin"
      case MySQLCollation("keybcs2", "Cp852", 73) => "keybcs2_bin"
      case MySQLCollation("koi8r", "KOI8_R", 74) => "koi8r_bin"
      case MySQLCollation("koi8u", "KOI8_R", 75) => "koi8u_bin"
      case MySQLCollation("latin2", "ISO8859_2", 77) => "latin2_bin"
      case MySQLCollation("latin5", "ISO8859_9", 78) => "latin5_bin"
      case MySQLCollation("latin7", "ISO-8859-13", 79) => "latin7_bin"
      case MySQLCollation("cp850", "Cp850", 80) => "cp850_bin"
      case MySQLCollation("cp852", "Cp852", 81) => "cp852_bin"
      case MySQLCollation("swe7", "Cp1252", 82) => "swe7_bin"
      case MySQLCollation("utf8", "UTF-8", 83) => "utf8_bin"
      case MySQLCollation("big5", "Big5", 84) => "big5_bin"
      case MySQLCollation("euckr", "EUC_KR", 85) => "euckr_bin"
      case MySQLCollation("gb2312", "EUC_CN", 86) => "gb2312_bin"
      case MySQLCollation("gbk", "GBK", 87) => "gbk_bin"
      case MySQLCollation("sjis", "SJIS", 88) => "sjis_bin"
      case MySQLCollation("tis620", "TIS620", 89) => "tis620_bin"
      case MySQLCollation("ucs2", "UnicodeBig", 90) => "ucs2_bin"
      case MySQLCollation("ujis", "EUC_JP", 91) => "ujis_bin"
      case MySQLCollation("geostd8", "Cp1252", 92) => "geostd8_general_ci"
      case MySQLCollation("geostd8", "Cp1252", 93) => "geostd8_bin"
      case MySQLCollation("latin1", "Cp1252", 94) => "latin1_spanish_ci"
      case MySQLCollation("cp932", "Cp932", 95) => "cp932_japanese_ci"
      case MySQLCollation("cp932", "Cp932", 96) => "cp932_bin"
      case MySQLCollation("eucjpms", "EUC_JP_Solaris", 97) => "eucjpms_japanese_ci"
      case MySQLCollation("eucjpms", "EUC_JP_Solaris", 98) => "eucjpms_bin"
      case MySQLCollation("cp1250", "Cp1250", 99) => "cp1250_polish_ci"
      case MySQLCollation("utf16", "UTF-16", 101) => "utf16_unicode_ci"
      case MySQLCollation("utf16", "UTF-16", 102) => "utf16_icelandic_ci"
      case MySQLCollation("utf16", "UTF-16", 103) => "utf16_latvian_ci"
      case MySQLCollation("utf16", "UTF-16", 104) => "utf16_romanian_ci"
      case MySQLCollation("utf16", "UTF-16", 105) => "utf16_slovenian_ci"
      case MySQLCollation("utf16", "UTF-16", 106) => "utf16_polish_ci"
      case MySQLCollation("utf16", "UTF-16", 107) => "utf16_estonian_ci"
      case MySQLCollation("utf16", "UTF-16", 108) => "utf16_spanish_ci"
      case MySQLCollation("utf16", "UTF-16", 109) => "utf16_swedish_ci"
      case MySQLCollation("utf16", "UTF-16", 110) => "utf16_turkish_ci"
      case MySQLCollation("utf16", "UTF-16", 111) => "utf16_czech_ci"
      case MySQLCollation("utf16", "UTF-16", 112) => "utf16_danish_ci"
      case MySQLCollation("utf16", "UTF-16", 113) => "utf16_lithuanian_ci"
      case MySQLCollation("utf16", "UTF-16", 114) => "utf16_slovak_ci"
      case MySQLCollation("utf16", "UTF-16", 115) => "utf16_spanish2_ci"
      case MySQLCollation("utf16", "UTF-16", 116) => "utf16_roman_ci"
      case MySQLCollation("utf16", "UTF-16", 117) => "utf16_persian_ci"
      case MySQLCollation("utf16", "UTF-16", 118) => "utf16_esperanto_ci"
      case MySQLCollation("utf16", "UTF-16", 119) => "utf16_hungarian_ci"
      case MySQLCollation("utf16", "UTF-16", 120) => "utf16_sinhala_ci"
      case MySQLCollation("utf16", "UTF-16", 121) => "utf16_german2_ci"
      case MySQLCollation("utf16", "UTF-16", 122) => "utf16_croatian_ci"
      case MySQLCollation("utf16", "UTF-16", 123) => "utf16_unicode_520_ci"
      case MySQLCollation("utf16", "UTF-16", 124) => "utf16_vietnamese_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 128) => "ucs2_unicode_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 129) => "ucs2_icelandic_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 130) => "ucs2_latvian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 131) => "ucs2_romanian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 132) => "ucs2_slovenian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 133) => "ucs2_polish_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 134) => "ucs2_estonian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 135) => "ucs2_spanish_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 136) => "ucs2_swedish_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 137) => "ucs2_turkish_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 138) => "ucs2_czech_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 139) => "ucs2_danish_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 140) => "ucs2_lithuanian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 141) => "ucs2_slovak_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 142) => "ucs2_spanish2_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 143) => "ucs2_roman_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 144) => "ucs2_persian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 145) => "ucs2_esperanto_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 146) => "ucs2_hungarian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 147) => "ucs2_sinhala_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 148) => "ucs2_german2_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 149) => "ucs2_croatian_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 150) => "ucs2_unicode_520_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 151) => "ucs2_vietnamese_ci"
      case MySQLCollation("ucs2", "UnicodeBig", 159) => "ucs2_general_mysql500_ci"
      case MySQLCollation("utf32", "UTF-32", 160) => "utf32_unicode_ci"
      case MySQLCollation("utf32", "UTF-32", 161) => "utf32_icelandic_ci"
      case MySQLCollation("utf32", "UTF-32", 162) => "utf32_latvian_ci"
      case MySQLCollation("utf32", "UTF-32", 163) => "utf32_romanian_ci"
      case MySQLCollation("utf32", "UTF-32", 164) => "utf32_slovenian_ci"
      case MySQLCollation("utf32", "UTF-32", 165) => "utf32_polish_ci"
      case MySQLCollation("utf32", "UTF-32", 166) => "utf32_estonian_ci"
      case MySQLCollation("utf32", "UTF-32", 167) => "utf32_spanish_ci"
      case MySQLCollation("utf32", "UTF-32", 168) => "utf32_swedish_ci"
      case MySQLCollation("utf32", "UTF-32", 169) => "utf32_turkish_ci"
      case MySQLCollation("utf32", "UTF-32", 170) => "utf32_czech_ci"
      case MySQLCollation("utf32", "UTF-32", 171) => "utf32_danish_ci"
      case MySQLCollation("utf32", "UTF-32", 172) => "utf32_lithuanian_ci"
      case MySQLCollation("utf32", "UTF-32", 173) => "utf32_slovak_ci"
      case MySQLCollation("utf32", "UTF-32", 174) => "utf32_spanish2_ci"
      case MySQLCollation("utf32", "UTF-32", 175) => "utf32_roman_ci"
      case MySQLCollation("utf32", "UTF-32", 176) => "utf32_persian_ci"
      case MySQLCollation("utf32", "UTF-32", 177) => "utf32_esperanto_ci"
      case MySQLCollation("utf32", "UTF-32", 178) => "utf32_hungarian_ci"
      case MySQLCollation("utf32", "UTF-32", 179) => "utf32_sinhala_ci"
      case MySQLCollation("utf32", "UTF-32", 180) => "utf32_german2_ci"
      case MySQLCollation("utf32", "UTF-32", 181) => "utf32_croatian_ci"
      case MySQLCollation("utf32", "UTF-32", 182) => "utf32_unicode_520_ci"
      case MySQLCollation("utf32", "UTF-32", 183) => "utf32_vietnamese_ci"
      case MySQLCollation("utf8", "UTF-8", 192) => "utf8_unicode_ci"
      case MySQLCollation("utf8", "UTF-8", 193) => "utf8_icelandic_ci"
      case MySQLCollation("utf8", "UTF-8", 194) => "utf8_latvian_ci"
      case MySQLCollation("utf8", "UTF-8", 195) => "utf8_romanian_ci"
      case MySQLCollation("utf8", "UTF-8", 196) => "utf8_slovenian_ci"
      case MySQLCollation("utf8", "UTF-8", 197) => "utf8_polish_ci"
      case MySQLCollation("utf8", "UTF-8", 198) => "utf8_estonian_ci"
      case MySQLCollation("utf8", "UTF-8", 199) => "utf8_spanish_ci"
      case MySQLCollation("utf8", "UTF-8", 200) => "utf8_swedish_ci"
      case MySQLCollation("utf8", "UTF-8", 201) => "utf8_turkish_ci"
      case MySQLCollation("utf8", "UTF-8", 202) => "utf8_czech_ci"
      case MySQLCollation("utf8", "UTF-8", 203) => "utf8_danish_ci"
      case MySQLCollation("utf8", "UTF-8", 204) => "utf8_lithuanian_ci"
      case MySQLCollation("utf8", "UTF-8", 205) => "utf8_slovak_ci"
      case MySQLCollation("utf8", "UTF-8", 206) => "utf8_spanish2_ci"
      case MySQLCollation("utf8", "UTF-8", 207) => "utf8_roman_ci"
      case MySQLCollation("utf8", "UTF-8", 208) => "utf8_persian_ci"
      case MySQLCollation("utf8", "UTF-8", 209) => "utf8_esperanto_ci"
      case MySQLCollation("utf8", "UTF-8", 210) => "utf8_hungarian_ci"
      case MySQLCollation("utf8", "UTF-8", 211) => "utf8_sinhala_ci"
      case MySQLCollation("utf8", "UTF-8", 212) => "utf8_german2_ci"
      case MySQLCollation("utf8", "UTF-8", 213) => "utf8_croatian_ci"
      case MySQLCollation("utf8", "UTF-8", 214) => "utf8_unicode_520_ci"
      case MySQLCollation("utf8", "UTF-8", 215) => "utf8_vietnamese_ci"
      case MySQLCollation("utf8", "UTF-8", 223) => "utf8_general_mysql500_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 224) => "utf8mb4_unicode_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 225) => "utf8mb4_icelandic_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 226) => "utf8mb4_latvian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 227) => "utf8mb4_romanian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 228) => "utf8mb4_slovenian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 229) => "utf8mb4_polish_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 230) => "utf8mb4_estonian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 231) => "utf8mb4_spanish_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 232) => "utf8mb4_swedish_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 233) => "utf8mb4_turkish_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 234) => "utf8mb4_czech_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 235) => "utf8mb4_danish_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 236) => "utf8mb4_lithuanian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 237) => "utf8mb4_slovak_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 238) => "utf8mb4_spanish2_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 239) => "utf8mb4_roman_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 240) => "utf8mb4_persian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 241) => "utf8mb4_esperanto_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 242) => "utf8mb4_hungarian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 243) => "utf8mb4_sinhala_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 244) => "utf8mb4_german2_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 245) => "utf8mb4_croatian_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 246) => "utf8mb4_unicode_520_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 247) => "utf8mb4_vietnamese_ci"
      case MySQLCollation("gb18030", "GB18030", 248) => "gb18030_chinese_ci"
      case MySQLCollation("gb18030", "GB18030", 249) => "gb18030_bin"
      case MySQLCollation("gb18030", "GB18030", 250) => "gb18030_unicode_520_ci"
      case MySQLCollation("utf8mb4", "UTF-8", 255) => "utf8mb4_0900_ai_ci"
      case _ => throw new IllegalArgumentException(s"Unknown MySQL collation: ${collation}")
    }

  }

  def getJavaCharsetByCollationId(collationId: Int): Charset = idToJavaCharsetMapping.get(collationId) match {
    case Some(value) => value
    case None => StandardCharsets.UTF_8
  }

  def getDefaultCollationFromCharsetName(charset: String): String =
    charsetToDefaultCollationMapping.get(charset) match {
      case Some(value) => value
      case None => throw new IllegalArgumentException(s"Unknown charset name: [$charset]")
    }

}