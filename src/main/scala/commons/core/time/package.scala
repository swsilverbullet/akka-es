package commons.core

import java.util.TimeZone

import org.joda.time.{DateTime, DateTimeZone, LocalDate, LocalTime}
import org.joda.time.format.DateTimeFormat

package object time {
  def localDate(value: String): LocalDate = LocalDate.parse(value, dateFormatter)
  def dateTime(value: String): DateTime = DateTime.parse(value, dateFormatter)
  def isoDate(value: String): DateTime = DateTime.parse(value, isoDateTimeFormatter)
  def isoDate(date: DateTime): String = isoDateTimeFormatter.print(date)
  def isoDate(date: LocalDate): String = isoDateTimeFormatter.print(date.toLocalDateTime(LocalTime.MIDNIGHT))
  def latestDateTime(left: DateTime, right: DateTime): DateTime = if (left.compareTo(right) >= 0) left else right
  def latestLocalTime(left: LocalTime, right: LocalTime): LocalTime = if (left.compareTo(right) >= 0) left else right
  def shortDate: String = shortDate(LocalDate.now)
  def shortDate(date: LocalDate): String = shortDateFormatter.print(date)
  def shortLocalTime(time: LocalTime): String = shortDateFormatter.print(time)
  def shortDateTime: String = shortTimeFormatter.print(DateTime.now)
  def longDateTime: String = longDateTimeFormatter.print(DateTime.now)
  def timestamp(value: String, timeZone: DateTimeZone): DateTime = DateTime.parse(value, timeStampFormatter.withZone(timeZone))
  def utcTimeZone = DateTimeZone.UTC
  def nowInTimeZone(timeZone: TimeZone): DateTime = DateTime.now(DateTimeZone.forTimeZone(timeZone))

  private val shortDateFormatter = DateTimeFormat.forPattern("yyyyMMdd")
  private val shortTimeFormatter = DateTimeFormat.forPattern("HHmmss")
  private val longDateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS")
  private val dateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd")
  private val isoDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
  private val timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd-HH:mm:ss.SSS")
}
