package tech.yankun.sdoob.driver.mysql.utils

import io.netty.buffer.ByteBuf

import java.nio.charset.Charset

object BufferUtils {
  private val TERMINAL: Byte = 0x00

  def readNullTerminatedString(buffer: ByteBuf, charset: Charset): String = {
    val len = buffer.bytesBefore(TERMINAL)
    val s = buffer.readCharSequence(len, charset).toString
    buffer.readByte()
    s
  }

  def readFixedLengthString(buffer: ByteBuf, length: Int, charset: Charset): String = {
    buffer.readCharSequence(length, charset).toString
  }

  def writeNullTerminatedString(buffer: ByteBuf, charSequence: CharSequence, charset: Charset): Unit = {
    buffer.writeCharSequence(charSequence, charset)
    buffer.writeByte(0)
  }

  def writeLengthEncodedInteger(buffer: ByteBuf, value: Long): Unit = {
    if (value < 251) {
      //  1-byte integer
      buffer.writeByte(value.asInstanceOf[Byte])
    } else if (value <= 0xFFFF) {
      //  0xFC + 2-byte integer
      buffer.writeByte(0xFC)
      buffer.writeShortLE(value.toInt)
    } else if (value < 0xFFFFFF) {
      // 0xFD + 3-byte integer
      buffer.writeByte(0xFD)
      buffer.writeMediumLE(value.toInt)
    } else {
      // 0xFE + 8-byte integer
      buffer.writeByte(0xFE)
      buffer.writeLongLE(value)
    }
  }

  def readLengthEncodedInteger(buffer: ByteBuf): Long = {
    val firstByte: Short = buffer.readUnsignedByte()
    firstByte match {
      case 0xFB => -1
      case 0xFC => buffer.readUnsignedShortLE()
      case 0xFD => buffer.readUnsignedMediumLE()
      case 0xFE => buffer.readLongLE()
      case _ => firstByte
    }
  }

  def writeLengthEncodedString(buffer: ByteBuf, value: String, charset: Charset): Unit = {
    val bytes: Array[Byte] = value.getBytes(charset)
    writeLengthEncodedInteger(buffer, bytes.length)
    buffer.writeBytes(bytes)
  }

  def readLengthEncodedString(buffer: ByteBuf, charset: Charset): String = {
    val length = readLengthEncodedInteger(buffer)
    readFixedLengthString(buffer, length.toInt, charset)
  }

  def readDecStrAsLong(len: Int, buffer: ByteBuf): Long = {
    var index = buffer.readerIndex()
    var value: Long = 0
    if (len > 0) {
      val to = index + len
      val neg = if (buffer.getByte(index) == '-') {
        index += 1
        true
      } else false
      while (index < to) {
        value = value * 10 + (buffer.getByte(index) - '0')
        index += 1
      }
      if (neg) value = -value
      buffer.skipBytes(len)
    }
    value
  }

}
