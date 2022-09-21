package tech.yankun.sdoob.driver.mysql.datatype

import io.netty.buffer.ByteBuf
import tech.yankun.sdoob.driver.mysql.MySQLCollation
import tech.yankun.sdoob.driver.mysql.protocol.ColumnDefinition
import tech.yankun.sdoob.driver.mysql.utils.BufferUtils

import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField.{HOUR_OF_DAY, MICRO_OF_SECOND, MINUTE_OF_HOUR, SECOND_OF_MINUTE}
import java.time.{Duration, LocalDate, LocalDateTime}

/**
 *
 */
object RowValueCodec {
  private val DEFAULT_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive.append(ISO_LOCAL_DATE)
    .appendLiteral(' ').appendValue(HOUR_OF_DAY, 2).appendLiteral(':')
    .appendValue(MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(SECOND_OF_MINUTE, 2)
    .appendFraction(MICRO_OF_SECOND, 0, 6, true).toFormatter

  def decodeText(dataType: DataType, collationId: Int, flags: Int, payload: ByteBuf): Any = {
    val length = BufferUtils.readLengthEncodedInteger(payload).toInt
    val value: Any = dataType match {
      case DataType.INT1 =>
        if (ColumnDefinition.isUnsignedNumeric(flags))
          BufferUtils.readDecStrAsLong(length, payload).toShort
        else BufferUtils.readDecStrAsLong(length, payload).toByte
      case DataType.YEAR => BufferUtils.readDecStrAsLong(length, payload).toShort
      case DataType.INT2 =>
        if (ColumnDefinition.isUnsignedNumeric(flags))
          BufferUtils.readDecStrAsLong(length, payload).toInt
        else BufferUtils.readDecStrAsLong(length, payload).toShort
      case DataType.INT3 => BufferUtils.readDecStrAsLong(length, payload).toInt
      case DataType.INT4 =>
        if (ColumnDefinition.isUnsignedNumeric(flags))
          BufferUtils.readDecStrAsLong(length, payload)
        else BufferUtils.readDecStrAsLong(length, payload).toInt
      case DataType.INT8 =>
        if (ColumnDefinition.isUnsignedNumeric(flags))
          readTextNumeric(collationId, payload, length)
        else BufferUtils.readDecStrAsLong(length, payload)
      case DataType.FLOAT => readTextFloat(collationId, payload, length)
      case DataType.DOUBLE => readTextDouble(collationId, payload, length)
      case DataType.BIT => readTextBit(payload, length)
      case DataType.NUMERIC => readTextNumeric(collationId, payload, length)
      case DataType.DATE => readTextDate(collationId, payload, length)
      case DataType.TIME => readTextTime(collationId, payload, length)
      case DataType.DATETIME | DataType.TIMESTAMP => readTextDateTime(collationId, payload, length)
      case DataType.JSON =>
        payload.skipBytes(length)
        null
      case DataType.GEOMETRY =>
        payload.skipBytes(length)
        null
      case DataType.STRING | DataType.VARSTRING => readString(collationId, payload, length)
      case DataType.BLOB => readTextBlob(payload, length)
      case _ =>
        payload.skipBytes(length)
        null
    }
    value
  }


  def readTextNumeric(collationId: Int, buffer: ByteBuf, len: Int): BigDecimal = {
    val text = readString(collationId, buffer, len)
    if (text == "NaN") null else new java.math.BigDecimal(text)
  }

  def readTextFloat(collationId: Int, buffer: ByteBuf, len: Int): Float =
    java.lang.Float.parseFloat(readString(collationId, buffer, len))

  def readTextDouble(collationId: Int, buffer: ByteBuf, len: Int): Double =
    java.lang.Double.parseDouble(readString(collationId, buffer, len))

  def readTextBit(buffer: ByteBuf, len: Int): Long = {
    var result: Long = 0
    var cursor = 0
    while (cursor < len) {
      result = buffer.readByte() | (result << 8)
      cursor += 1
    }
    result
  }

  def readTextDate(collationId: Int, buffer: ByteBuf, len: Int): LocalDate = {
    val cs = readString(collationId, buffer, len)
    if (cs == "0000-00-00") null else LocalDate.parse(cs)
  }

  def readTextTime(collationId: Int, buffer: ByteBuf, len: Int): Duration = {
    var time = readString(collationId, buffer, len)
    val neg = time.startsWith("-")
    if (neg) time = time.substring(1)
    val elems = time.split(":")
    assert(elems.length == 3, "Invalid time format")
    val Array(hour, minute, second) = elems.map(_.toInt)
    val nanos: Long = if (elems(2).length > 2) {
      (1000000000D * ("0." + elems(2).substring(3)).toDouble).toLong
    } else 0

    if (neg) Duration.ofHours(-hour).minusMinutes(minute).minusSeconds(second).minusNanos(nanos) else
      Duration.ofHours(hour).plusMinutes(minute).plusSeconds(second).plusNanos(nanos)
  }

  def readTextDateTime(collationId: Int, buffer: ByteBuf, len: Int): LocalDateTime = {
    val date = readString(collationId, buffer, len)
    if (date == "0000-00-00 00:00:00") null else LocalDateTime.parse(date, DEFAULT_FORMATTER)
  }

  def readTextBlob(buffer: ByteBuf, len: Int): Array[Byte] = {
    val buf = new Array[Byte](len)
    buffer.readBytes(buf)
    buf
  }

  private def readString(collationId: Int, buffer: ByteBuf, len: Int): String = {
    val charset = MySQLCollation.getJavaCharsetByCollationId(collationId)
    buffer.readCharSequence(len, charset).toString
  }

}
