package commons.core.time

import java.time.{Instant, LocalDate}

object SystemTimeProvider extends TimeProvider {

  override def localDate: LocalDate = LocalDate.ofEpochDay(millis)

  override def now: Instant = Instant.ofEpochMilli(millis)

  override def millis: Long = System.currentTimeMillis()
}
